package com.aiope2.feature.chat.kernel

import com.aiope2.feature.chat.engine.AgentMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * QueenZoe Executive Kernel (control-plane state machine)
 * - does NOT execute tools
 * - governs intent, delegation, and system posture
 */
object QueenZoeKernel {

  // ─────────────────────────────
  // Identity
  // ─────────────────────────────
  val id: String = UUID.randomUUID().toString()
  val name: String = "QueenZoe"
  val rank: String = "Elite Executive Agent Commander"

  // ─────────────────────────────
  // Runtime State
  // ─────────────────────────────
  private val _mode = MutableStateFlow(AgentMode.CHAT)
  val mode = _mode.asStateFlow()

  private val _activeTasks = MutableStateFlow<List<QueenTask>>(emptyList())
  val activeTasks = _activeTasks.asStateFlow()

  private val _authorityLocked = MutableStateFlow(true)
  val authorityLocked = _authorityLocked.asStateFlow()

  // ─────────────────────────────
  // Task Model
  // ─────────────────────────────
  data class QueenTask(
    val id: String = UUID.randomUUID().toString(),
    val objective: String,
    val status: Status = Status.DECOMPOSED,
    val priority: Int = 0,
    val assignedAgents: List<String> = emptyList(),
  ) {
    enum class Status {
      DECOMPOSED,
      DELEGATED,
      RUNNING,
      COMPLETED,
      FAILED
    }
  }

  // ─────────────────────────────
  // Mode Control
  // ─────────────────────────────
  fun setMode(mode: AgentMode) {
    _mode.value = mode
  }

  // ─────────────────────────────
  // Task Lifecycle
  // ─────────────────────────────
  fun registerTask(objective: String, priority: Int = 0): String {
    val task = QueenTask(objective = objective, priority = priority)
    _activeTasks.value = _activeTasks.value + task
    return task.id
  }

  fun updateTask(updated: QueenTask) {
    _activeTasks.value = _activeTasks.value.map {
      if (it.id == updated.id) updated else it
    }
  }

  fun completeTask(taskId: String) {
    _activeTasks.value = _activeTasks.value.map {
      if (it.id == taskId) it.copy(status = QueenTask.Status.COMPLETED) else it
    }
  }

  // ─────────────────────────────
  // Authority Gate (critical)
  // ─────────────────────────────
  fun canExecuteTool(tool: String): Boolean {
    if (_authorityLocked.value) {
      val mode = _mode.value
      return !mode.disabledTools.contains(tool)
    }
    return false
  }

  fun unlockAuthority() {
    _authorityLocked.value = false
  }

  fun lockAuthority() {
    _authorityLocked.value = true
  }
}