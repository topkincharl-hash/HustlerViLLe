package com.aiope2.feature.chat.orchestration

/**
 * QueenZoe Execution Planner
 *
 * Converts intent → structured execution graph
 * Decides:
 * - decomposition
 * - parallelization
 * - subagent allocation
 */

data class ExecutionPlan(
    val rootTask: String,
    val steps: List<ExecutionStep>,
    val strategy: ExecutionStrategy
)

data class ExecutionStep(
    val id: String,
    val goal: String,
    val dependencies: List<String> = emptyList(),
    val mode: StepMode,
    val parallelizable: Boolean = true
)

enum class StepMode {
    SUBAGENT_SEARCH,
    SUBAGENT_ANALYSIS,
    SUBAGENT_REASONING,
    TOOL_DIRECT,
    SYSTEM_ACTION
}

enum class ExecutionStrategy {
    SEQUENTIAL,
    PARALLEL,
    HYBRID
}

class ExecutionPlanner {

    fun plan(intent: String): ExecutionPlan {

        val normalized = intent.lowercase()

        val steps = mutableListOf<ExecutionStep>()

        // ─────────────────────────────
        // INTENT DECOMPOSITION RULES
        // ─────────────────────────────

        if (normalized.contains("analyze") || normalized.contains("research")) {

            steps += ExecutionStep(
                id = "s1",
                goal = "Gather relevant data sources",
                mode = StepMode.SUBAGENT_SEARCH
            )

            steps += ExecutionStep(
                id = "s2",
                goal = "Analyze collected information",
                dependencies = listOf("s1"),
                mode = StepMode.SUBAGENT_ANALYSIS
            )

            steps += ExecutionStep(
                id = "s3",
                goal = "Synthesize final conclusion",
                dependencies = listOf("s2"),
                mode = StepMode.SUBAGENT_REASONING,
                parallelizable = false
            )

            return ExecutionPlan(
                rootTask = intent,
                steps = steps,
                strategy = ExecutionStrategy.HYBRID
            )
        }

        if (normalized.contains("run") || normalized.contains("execute")) {

            steps += ExecutionStep(
                id = "s1",
                goal = "Direct tool execution",
                mode = StepMode.TOOL_DIRECT,
                parallelizable = false
            )

            return ExecutionPlan(
                rootTask = intent,
                steps = steps,
                strategy = ExecutionStrategy.SEQUENTIAL
            )
        }

        // DEFAULT PATH (light reasoning)
        steps += ExecutionStep(
            id = "s1",
            goal = "Reason about request",
            mode = StepMode.SUBAGENT_REASONING
        )

        return ExecutionPlan(
            rootTask = intent,
            steps = steps,
            strategy = ExecutionStrategy.PARALLEL
        )
    }
}