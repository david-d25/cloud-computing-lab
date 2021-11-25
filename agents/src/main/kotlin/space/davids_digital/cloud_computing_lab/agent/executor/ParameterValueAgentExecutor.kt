package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType.STRING
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutorParameter
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException

@AgentExecutor("Текст из параметра")
@AgentExecutorParameter("Текст", "data", STRING,true)
class ParameterValueAgentExecutor(context: AgentContext) : AbstractAgentExecutor(context) {
    override fun execute() {
        val data = context.getParameter("data") ?: throw AgentExecutionException("Please, provide 'data' parameter")
        context.clearData()
        context.submitData(data)
    }
}