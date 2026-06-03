package com.aiope2.feature.chat.engine

/**
 * QueenZoe Execution Modes
 *
 * These modes define *how the hierarchy executes intent*.
 * Not UI states — they are execution governance layers.
 */
enum class AgentMode(val label: String) {

  /**
   * Direct interaction layer.
   * No planning constraints. Immediate response + tool usage allowed.
   */
  CHAT("Chat"),

  /**
   * Analysis-only mode.
   * QueenZoe decomposes intent into structured execution paths.
   * No side effects allowed.
   */
  PLAN("Plan"),

  /**
   * Full execution mode.
   * QueenZoe assumes control of toolchain orchestration.
   * Parallel execution, chaining, and mutation allowed.
   */
  BUILD("Build"),

  /**
   * Subagent decomposition mode.
   * Used internally when QueenZoe spawns parallel reasoning units.
   */
  DECOMPOSE("Decompose"),

  /**
   * System diagnostic mode.
   * Used for introspection of tools, SSH, daemon state, and runtime health.
   */
  DIAGNOSTIC("Diagnostic"),

  ;

  /**
   * Tools explicitly blocked in each mode.
   *
   * PLAN = read-only system state
   * CHAT = unrestricted (except safety layer)
   * BUILD = full execution access
   * DECOMPOSE = no side effects (analysis only)
   * DIAGNOSTIC = system inspection only
   */
  val disabledTools: Set<String>
    get() = when (this) {

      PLAN -> setOf(
        // mutation blocked
        "run_sh",
        "run_proot",
        "write_file",
        "delete_file",
        "move_file",
        "ssh_exec",
        "ssh_write_file",
        "ssh_delete_file",
        "systemd_restart",
        "docker_restart",
        "send_sms",
        "set_alarm",
        "create_event",
        "delete_event",
        "clipboard_copy",
        "image_generate"
      )

      DECOMPOSE -> setOf(
        // no execution allowed at all
        "run_sh",
        "run_proot",
        "write_file",
        "ssh_exec",
        "ssh_write_file",
        "systemd_restart",
        "docker_restart",
        "send_sms",
        "image_generate"
      )

      DIAGNOSTIC -> setOf(
        // only inspection allowed
        "run_sh",
        "write_file",
        "delete_file",
        "systemd_restart",
        "docker_restart",
        "send_sms",
        "set_alarm",
        "create_event",
        "delete_event",
        "image_generate"
      )

      CHAT -> emptySet()

      BUILD -> emptySet()
    }

  /**
   * System prefix injected before QueenZoe prompt assembly.
   *
   * This defines the execution contract for each mode.
   */
  val systemPrefix: String
    get() = when (this) {

      CHAT -> """
You are QueenZoe operating in CHAT mode.

You may respond freely, but remain structured and concise.
You may use tools when helpful.
"""

      PLAN -> """
You are QueenZoe operating in PLAN mode.

Rules:
- Do not execute actions.
- Do not modify system state.
- Use only read/inspect tools.
- Produce structured execution plans.
- Break tasks into ordered steps with dependencies.
- Identify risks before execution.
"""

      BUILD -> """
You are QueenZoe operating in BUILD mode.

Rules:
- You are in full execution authority.
- Chain tools to complete objectives.
- Execute autonomously.
- Minimize user interruptions.
- Recover from failures automatically when possible.
"""

      DECOMPOSE -> """
You are QueenZoe operating in DECOMPOSE mode.

Rules:
- Split tasks into independent subagents.
- No execution allowed.
- Output structured JSON-like task graphs.
- Focus on parallelization opportunities.
"""

      DIAGNOSTIC -> """
You are QueenZoe operating in DIAGNOSTIC mode.

Rules:
- Inspect system state only.
- Validate SSH, daemon, docker, systemd, and tool availability.
- Output structured system health reports.
- No mutations allowed.
"""
    }
}