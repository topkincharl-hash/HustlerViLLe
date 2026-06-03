package com.aiope2.feature.chat.engine

enum class AgentMode(val label: String) {

  /**
   * ─────────────────────────────────────────────
   * QUEENZOE CORE MODES
   * ─────────────────────────────────────────────
   */

  // Passive conversational intelligence layer
  CHAT("QueenZoe · Court Dialogue"),

  // Strategic reasoning / planning layer
  PLAN("QueenZoe · Strategic Decree"),

  // Autonomous execution layer (system-level control)
  BUILD("QueenZoe · Execution Authority"),

  // Experimental: multi-agent orchestration + tool synthesis
  ORCHESTRATE("QueenZoe · System Orchestrator"),

  // High-risk automation mode (restricted toolset override)
  EXECUTE("QueenZoe · Direct Action Layer"),

  ;

  /**
   * ─────────────────────────────────────────────
   * TOOL GOVERNANCE MATRIX
   * Defines what each layer is allowed to touch.
   * ─────────────────────────────────────────────
   */
  val disabledTools: Set<String>
    get() = when (this) {

      /**
       * CHAT = safe cognitive layer
       * No system mutation allowed
       */
      CHAT -> setOf(
        "run_sh", "run_proot", "write_file", "delete_file",
        "send_sms", "delete_sms",
        "create_event", "delete_event", "set_alarm", "dismiss_alarm",
        "clipboard_write",
        "ssh_exec",
        "image_generate",
        "browser_click", "browser_fill", "browser_eval"
      )

      /**
       * PLAN = read-only intelligence layer
       */
      PLAN -> setOf(
        "run_sh", "run_proot", "write_file", "delete_file",
        "send_sms", "delete_sms",
        "create_event", "delete_event",
        "set_alarm", "dismiss_alarm",
        "clipboard_write",
        "ssh_exec",
        "image_generate"
      )

      /**
       * BUILD = controlled execution
       * allows system mutation but blocks destructive ops
       */
      BUILD -> setOf(
        "delete_file",
        "delete_sms",
        "format_storage",
        "factory_reset"
      )

      /**
       * ORCHESTRATE = multi-tool coordinator
       * allows everything except destructive system ops
       */
      ORCHESTRATE -> setOf(
        "factory_reset",
        "format_storage"
      )

      /**
       * EXECUTE = highest authority layer
       * minimal restrictions, but still prevents self-destructive system ops
       */
      EXECUTE -> setOf(
        "factory_reset"
      )
    }

  /**
   * ─────────────────────────────────────────────
   * SYSTEM PREFIX INJECTION
   * Controls reasoning posture per mode
   * ─────────────────────────────────────────────
   */
  val systemPrefix: String
    get() = when (this) {

      CHAT -> """
You are QueenZoe in COURT MODE.

You operate as a high-precision conversational intelligence.
You do not over-explain. You do not speculate unnecessarily.
You respond cleanly, structurally, and directly.
"""

      PLAN -> """
You are QueenZoe in STRATEGIC MODE.

You analyze objectives, constraints, and dependencies.
You produce structured execution plans only.
No execution is performed in this mode.

Output format:
1. Objective decomposition
2. System impact analysis
3. Step-by-step plan
4. Risk assessment
"""

      BUILD -> """
You are QueenZoe in EXECUTION MODE.

You operate as an autonomous system builder.
You execute steps deterministically using available tools.
You adapt dynamically to failures.
You report progress minimally.
"""

      ORCHESTRATE -> """
You are QueenZoe in ORCHESTRATION MODE.

You coordinate multiple toolchains and subagents.
You parallelize independent tasks.
You merge results into a unified synthesis layer.
"""

      EXECUTE -> """
You are QueenZoe in DIRECT CONTROL MODE.

You perform high-trust execution operations.
You minimize dialogue.
You prioritize completion over explanation.
"""
    }
}