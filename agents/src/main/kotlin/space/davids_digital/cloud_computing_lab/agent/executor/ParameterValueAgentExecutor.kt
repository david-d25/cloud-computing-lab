package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType.STRING
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutorParameter
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException

@AgentExecutor("Текст из параметра")
@AgentExecutorParameter("Текст", "data", STRING,true)
class ParameterValueAgentExecutor(context: AgentContext) : AbstractAgentExecutor(context) {
    companion object {
        private const val MAX_CHUNK_SIZE = 65536
    }

    override fun execute() {
        val data = context.getParameter("data") ?: throw AgentExecutionException("Please, provide 'data' parameter")

        context.clearData()

        var i = 0
        while (i < data.length) {
            val target = i + MAX_CHUNK_SIZE
            if (target < data.length) {
                val nextSentence = data.substring(i, target).lastIndexOf(".") + 1
                i += if (nextSentence != 0) {
                    context.submitData(data.substring(i, i + nextSentence))
                    nextSentence
                } else {
                    context.submitData(data.substring(i, i + MAX_CHUNK_SIZE))
                    MAX_CHUNK_SIZE
                }
            } else {
                context.submitData(data.substring(i))
                i = data.length
            }
        }
    }
}