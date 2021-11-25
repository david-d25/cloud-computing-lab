package space.davids_digital.cloud_computing_lab.agent.annotation

import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType
import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType.STRING

annotation class AgentExecutorParameter(
    val title: String,
    val name: String,
    val type: AgentExecutorParameterType = STRING,
    val required: Boolean = false
)
