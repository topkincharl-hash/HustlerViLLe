package com.aiope2.feature.chat.engine

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Base64
import java.util.UUID

class QueenZoeBuildDispatcher(
  private val client: OkHttpClient,
  private val token: String,
  private val owner: String,
  private val repo: String,
) {

  private val JSON = "application/json; charset=utf-8".toMediaType()

  data class Intent(
    val type: String,
    val modules: List<String>,
    val commit: String,
    val branch: String = "main",
  )

  fun dispatch(intent: Intent): String {

    val buildId = UUID.randomUUID().toString()

    val payload = JSONObject().apply {
      put("buildId", buildId)
      put("agentMode", "BUILD")

      put("source", JSONObject().apply {
        put("branch", intent.branch)
        put("commit", intent.commit)
      })

      put("build", JSONObject().apply {
        put("type", intent.type)
        put("modules", intent.modules)
      })

      put("output", JSONObject().apply {
        put("artifactType", "apk")
        put("uploadRelease", true)
      })
    }

    val encoded = Base64.getEncoder()
      .encodeToString(payload.toString().toByteArray())

    val body = JSONObject().apply {
      put("ref", "main")
      put("inputs", JSONObject().apply {
        put("buildIntent", encoded)
      })
    }

    val request = Request.Builder()
      .url("https://api.github.com/repos/$owner/$repo/actions/workflows/release.yml/dispatches")
      .addHeader("Authorization", "Bearer $token")
      .addHeader("Accept", "application/vnd.github+json")
      .post(body.toString().toRequestBody(JSON))
      .build()

    client.newCall(request).execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("Dispatch failed: ${resp.code} ${resp.body?.string()}")
      }
    }

    return buildId
  }
}