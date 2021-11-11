package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.AgentParameterType
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException

@AgentExecutor("File")
class FileAgentExecutor(context: AgentContext): AbstractAgentExecutor(context) {
    override fun getParameterTypesMap() = mapOf(
        "file" to AgentParameterType.FILE
    )

    override fun execute() {
        val fileContents = context.getParameter("file") ?: throw AgentExecutionException("Please, specify a file")
        context.clearData()
        context.submitData(fileContents)
    }
}