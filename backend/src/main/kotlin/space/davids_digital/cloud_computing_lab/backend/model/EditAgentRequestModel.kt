package space.davids_digital.cloud_computing_lab.backend.model

data class EditAgentRequestModel(
    val id: Int,
    val name: String? = null,
    val type: String? = null,
    val visible: Boolean? = null,
    val sensitive: Boolean? = null,
    val updatePeriodSeconds: Long? = null,
    val parameters: Map<String, String>? = null
)
