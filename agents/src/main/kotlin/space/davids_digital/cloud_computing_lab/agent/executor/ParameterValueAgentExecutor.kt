package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException

@AgentExecutor("Parameter Value")
class ParameterValueAgentExecutor(context: AgentContext) : AbstractAgentExecutor(context) {
    override fun execute() {
        val data = context.getParameter("data") ?: throw AgentExecutionException("Please, provide 'data' parameter")
        context.clearData()
        context.submitData(data)
    }
}