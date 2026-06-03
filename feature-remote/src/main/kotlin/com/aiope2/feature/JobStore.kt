package com.aiope.daemon.jobs

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class Job(
    val id: String = UUID.randomUUID().toString(),
    val command: String,
    var status: JobStatus = JobStatus.RUNNING,
    var stdout: StringBuilder = StringBuilder(),
    var stderr: StringBuilder = StringBuilder(),
    var exitCode: Int? = null
)

enum class JobStatus {
    RUNNING,
    COMPLETED,
    FAILED,
    KILLED
}

object JobStore {

    private val jobs = ConcurrentHashMap<String, Job>()

    fun create(command: String): Job {
        val job = Job(command = command)
        jobs[job.id] = job
        return job
    }

    fun get(id: String): Job? = jobs[id]

    fun update(job: Job) {
        jobs[job.id] = job
    }

    fun all(): List<Job> = jobs.values.toList()

    fun remove(id: String) {
        jobs.remove(id)
    }
}