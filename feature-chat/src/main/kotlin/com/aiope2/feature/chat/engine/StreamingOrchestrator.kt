 package com.aiope2.feature.chat.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StreamingOrchestrator(
  private val baseUrl: String,
  private val apiKey: String,
  private val model: String,
  private val tools: List<ToolDef> = emptyList(),
  private val agentMode: AgentMode = AgentMode.CHAT,
  private val onToolCall: suspend (String, Map<String, Any?>) -> String = { _, _ -> "" },
) {

  data class ToolDef(
    val name: String,
    val description: String,
    val parameters: JSONObject
  )

  companion object {

    private val client = SafeOkHttp.builder()
      .connectTimeout(15, TimeUnit.SECONDS)
      .readTimeout(5, TimeUnit.MINUTES)
      .writeTimeout(30, TimeUnit.SECONDS)
      .callTimeout(0, TimeUnit.SECONDS)
      .retryOnConnectionFailure(true)
      .protocols(listOf(okhttp3.Protocol.HTTP_1_1))
      .connectionPool(okhttp3.ConnectionPool(0, 1, TimeUnit.SECONDS))
      .build()

    private val JSON_MT = "application/json; charset=utf-8".toMediaType()
    private const val MAX_RETRIES = 3

    private fun isToolAllowed(mode: AgentMode, tool: String): Boolean {
      return tool !in mode.disabledTools
    }
  }

  fun stream(
    messages: List<Pair<String, String>>,
    imageBase64s: List<String> = emptyList(),
  ): Flow<ChatStreamChunk> = callbackFlow {

    val rawMessages = messages.map {
      JSONObject().put("role", it.first).put("content", it.second)
    }.toMutableList()

    var contentSoFar = StringBuilder()

    var retries = 0
    var maxRounds = 140

    while (maxRounds-- > 0) {

      val body = buildRequestBody(rawMessages)

      var toolAcc = mutableMapOf<Int, MutableMap<String, String>>()
      var sseError: String? = null
      var done = false

      val request = Request.Builder()
        .url("${baseUrl.trimEnd('/')}/chat/completions")
        .header("Content-Type", "application/json")
        .header("Accept", "text/event-stream")
        .apply { if (apiKey.isNotBlank()) header("Authorization", "Bearer $apiKey") }
        .post(body.toRequestBody(JSON_MT))
        .build()

      val factory = EventSources.createFactory(client)
      val latch = java.util.concurrent.CountDownLatch(1)

      val eventSource = factory.newEventSource(
        request,
        object : EventSourceListener() {

          override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {

            if (data == "[DONE]") {
              done = true
              latch.countDown()
              return
            }

            try {
              val json = JSONObject(data)
              val choices = json.optJSONArray("choices") ?: return
              if (choices.length() == 0) return

              val delta = choices.getJSONObject(0).optJSONObject("delta") ?: return

              val content = delta.optString("content", "")

              if (content.isNotBlank()) {
                contentSoFar.append(content)
                trySend(ChatStreamChunk(content = content))
              }

              val tcArr = delta.optJSONArray("tool_calls")

              if (tcArr != null) {
                for (i in 0 until tcArr.length()) {

                  val tc = tcArr.getJSONObject(i)
                  val idx = tc.optInt("index", 0)

                  val acc = toolAcc.getOrPut(idx) {
                    mutableMapOf("id" to "", "name" to "", "args" to "")
                  }

                  tc.optString("id", "").let { if (it.isNotBlank()) acc["id"] = it }

                  tc.optJSONObject("function")?.let { fn ->
                    fn.optString("name", "").let { acc["name"] = it }
                    fn.optString("arguments", "").let { acc["args"] += it }
                  }
                }
              }

            } catch (e: Exception) {
              sseError = e.message
            }
          }

          override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
            sseError = t?.message ?: "stream failure"
            latch.countDown()
          }

          override fun onClosed(eventSource: EventSource) {
            latch.countDown()
          }
        }
      )

      latch.await(180, TimeUnit.SECONDS)
      eventSource.cancel()

      if (sseError != null) break

      // TOOL EXECUTION LAYER (QUEENZOE CONTROL ENFORCEMENT)
      if (toolAcc.isNotEmpty()) {

        val calls = toolAcc.map { (_, acc) ->
          ToolCallInfo(
            id = acc["id"] ?: "",
            name = acc["name"] ?: "",
            arguments = emptyMap()
          )
        }

        val filtered = calls.filter {
          isToolAllowed(agentMode, it.name)
        }

        send(ChatStreamChunk(toolCalls = filtered))

        val results = filtered.map {
          ToolResultInfo(
            id = it.id,
            name = it.name,
            arguments = it.arguments,
            result = onToolCall(it.name, it.arguments)
          )
        }

        send(ChatStreamChunk(toolResults = results))

        rawMessages.add(
          JSONObject().apply {
            put("role", "assistant")
            put("tool_calls", JSONArray())
          }
        )

        continue
      }

      send(ChatStreamChunk(isDone = true))
      close()
      return@callbackFlow
    }

    send(ChatStreamChunk(isDone = true))
    close()

    awaitClose {}
  }.flowOn(Dispatchers.IO)

  private fun buildRequestBody(messages: List<JSONObject>): String {
    val body = JSONObject()
    body.put("model", model)
    body.put("stream", true)
    body.put("messages", JSONArray(messages))

    if (tools.isNotEmpty()) {
      body.put(
        "tools",
        JSONArray().apply {
          tools.forEach {
            put(
              JSONObject().put("type", "function").put(
                "function",
                JSONObject()
                  .put("name", it.name)
                  .put("description", it.description)
                  .put("parameters", it.parameters)
              )
            )
          }
        }
      )
    }

    return body.toString()
  }
}