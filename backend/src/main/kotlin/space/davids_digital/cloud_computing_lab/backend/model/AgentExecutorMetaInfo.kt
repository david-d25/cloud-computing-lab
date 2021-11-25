package space.davids_digital.cloud_computing_lab.backend.model

data class AgentExecutorMetaInfo(
    val type: String,
    val title: String,
    val parameters: List<AgentExecutorParameterMetaInfo>
)
