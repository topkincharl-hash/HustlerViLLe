package com.aiope2.feature.chat.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    // ─────────────────────────────
    // LOAD SETTINGS
    // ─────────────────────────────
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
        scope.launch {
            dao.upsertSetting(SettingsKvEntity(key, value))
        }
    }

    val bgActive = com.aiope2.feature.chat.theme.LocalThemeState.current.useBackground

    Scaffold(
        containerColor =
            if (bgActive) Color.Transparent
            else MaterialTheme.colorScheme.background,

        contentColor = MaterialTheme.colorScheme.onSurface,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "QueenZoe Core Control",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
                        Icon(Icons.Default.Refresh, "Reset all")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        if (bgActive) Color.Transparent
                        else MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->

        if (!loaded) return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ─────────────────────────────
            // SECTIONS
            // ─────────────────────────────
            AGENT_SECTIONS.forEach { section ->

                item(key = "header_${section.key}") {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = {
                            section.subsections.forEach { sub ->
                                save("$AGENT_PREFIX${sub.key}", sub.default)
                            }
                        }) {
                            Icon(
                                Icons.Default.Refresh,
                                "Reset section",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(section.subsections, key = { it.key }) { sub ->

                    val key = "$AGENT_PREFIX${sub.key}"

                    OutlinedTextField(
                        value = values[key] ?: "",
                        onValueChange = { save(key, it) },
                        label = { Text(sub.label) },
                        placeholder = {
                            Text(sub.hint, fontSize = 12.sp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        minLines = 2,
                        maxLines = 10
                    )
                }
            }

            // ─────────────────────────────
            // AUTO RUN CONTROL (QUEENZOE EXECUTION BEHAVIOR)
            // ─────────────────────────────
            item(key = "auto_run_prompt") {

                Text(
                    "Execution Control",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 18.dp, bottom = 6.dp)
                )

                OutlinedTextField(
                    value = values["agent_auto_run_prompt"] ?: "continue",
                    onValueChange = { save("agent_auto_run_prompt", it) },
                    label = { Text("Auto-run trigger") },
                    placeholder = {
                        Text("Controls automatic execution continuation", fontSize = 12.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    singleLine = true
                )
            }
        }
    }
}

/**
 * Builds QueenZoe system prompt from stored state
 */
internal suspend fun buildAgentPrompt(dao: ChatDao): String = buildString {

    AGENT_SECTIONS.forEach { section ->

        val parts = section.subsections.mapNotNull { sub ->
            dao.getSetting("$AGENT_PREFIX${sub.key}") ?: sub.default
        }.filter { it.isNotBlank() }

        if (parts.isNotEmpty()) {
            append("## ${section.title}\n")
            append(parts.joinToString("\n\n"))
            append("\n\n")
        }
    }

}.trimEnd()