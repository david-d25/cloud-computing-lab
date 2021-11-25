package space.davids_digital.cloud_computing_lab.backend.model

import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType

data class AgentExecutorParameterMetaInfo(
    val name: String,
    val title: String,
    val type: AgentExecutorParameterType,
    val required: Boolean
)
