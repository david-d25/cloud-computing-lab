package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentParameterType
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext

abstract class AbstractAgentExecutor(protected val context: AgentContext) {
    abstract fun getParameterTypesMap(): Map<String, AgentParameterType>
    abstract fun execute()
}