package space.davids_digital.cloud_computing_lab.backend.model

import java.time.Instant

data class Agent (
    val id: Int,
    val type: String,
    val name: String,
    val status: AgentStatus,
    val visible: Boolean,
    val updatePeriodSeconds: Long?,
    val lastUpdateTimestamp: Instant?,
    val sensitive: Boolean,
    val properties: Map<String, String>,
    val memory: Map<String, String>
)