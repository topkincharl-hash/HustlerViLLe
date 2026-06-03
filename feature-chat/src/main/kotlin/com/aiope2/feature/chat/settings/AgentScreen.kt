package com.aiope2.feature.chat.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiope2.feature.chat.db.ChatDao
import com.aiope2.feature.chat.db.SettingsKvEntity
import kotlinx.coroutines.launch

internal const val AGENT_PROMPT_KEY = "agent_prompt"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentScreen(
  dao: ChatDao,
  onBack: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val values = remember { mutableStateMapOf<String, String>() }
  var loaded by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    AGENT_SECTIONS.forEach { section ->
      section.subsections.forEach { sub ->
        val key = "$AGENT_PREFIX${sub.key}"
        values[key] = dao.getSetting(key) ?: sub.default
      }
    }
    values["agent_auto_run_prompt"] =
      dao.getSetting("agent_auto_run_prompt") ?: "continue"
    loaded = true
  }

  fun save(key: String, value: String) {
    values[key] = value
    scope.launch { dao.upsertSetting(SettingsKvEntity(key, value)) }
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      TopAppBar(
        title = {
          Text(
            "QueenZoe Core",
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, null)
          }
        },
        actions = {
          IconButton(onClick = {
            AGENT_SECTIONS.forEach { section ->
              section.subsections.forEach { sub ->
                save("$AGENT_PREFIX${sub.key}", sub.default)
              }
            }
          }) {
            Icon(Icons.Default.Refresh, "Reset")
          }
        }
      )
    }
  ) { padding ->

    if (!loaded) return@Scaffold

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

      AGENT_SECTIONS.forEach { section ->

        item("section_${section.key}") {
          Column(modifier = Modifier.fillMaxWidth()) {

            Row(
              Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(Modifier.weight(1f)) {
                Text(
                  section.title,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  section.description,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              IconButton(onClick = {
                section.subsections.forEach { sub ->
                  save("$AGENT_PREFIX${sub.key}", sub.default)
                }
              }) {
                Icon(Icons.Default.Refresh, null)
              }
            }
          }
        }

        items(section.subsections) { sub ->
          val key = "$AGENT_PREFIX${sub.key}"

          OutlinedTextField(
            value = values[key] ?: "",
            onValueChange = { save(key, it) },
            label = { Text(sub.label) },
            placeholder = { Text(sub.hint) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 8
          )
        }
      }

      item {
        Spacer(Modifier.height(12.dp))

        Text(
          "Auto Execution Layer",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
          value = values["agent_auto_run_prompt"] ?: "continue",
          onValueChange = { save("agent_auto_run_prompt", it) },
          label = { Text("Auto-run trigger") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
      }
    }
  }
}

/** Builds QueenZoe system prompt */
internal suspend fun buildAgentPrompt(dao: ChatDao): String = buildString {
  AGENT_SECTIONS.forEach { section ->
    val content = section.subsections.mapNotNull { sub ->
      val v = dao.getSetting("$AGENT_PREFIX${sub.key}") ?: sub.default
      v.takeIf { it.isNotBlank() }
    }

    if (content.isNotEmpty()) {
      appendLine("## ${section.title}")
      content.forEach { appendLine(it).appendLine() }
      appendLine()
    }
  }
}.trimEnd()