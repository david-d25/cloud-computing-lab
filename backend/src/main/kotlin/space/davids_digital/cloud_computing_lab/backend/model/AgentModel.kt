package space.davids_digital.cloud_computing_lab.backend.model

import java.time.Instant

data class AgentModel (
    val id: Int,
    val type: String,
    val name: String,
    val status: AgentStatusModel,
    val visible: Boolean,
    val updatePeriodSeconds: Long?,
    val lastUpdateTimestamp: Instant?,
    val lastAppliedDataEntry: Long?,
    val sensitive: Boolean,
    val parameters: Map<String, String>,
    val memory: Map<String, String>
)