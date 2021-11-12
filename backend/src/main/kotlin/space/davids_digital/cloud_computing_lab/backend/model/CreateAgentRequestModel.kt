package space.davids_digital.cloud_computing_lab.backend.model

data class CreateAgentRequestModel(
    val name: String,
    val type: String,
    val sensitive: Boolean,
    val visible: Boolean,
    val updatePeriodSeconds: Long?,
    val parameters: Map<String, String>
)
