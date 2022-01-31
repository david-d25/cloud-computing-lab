package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutorParameter
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException
import java.io.File
import java.net.URL
import java.nio.charset.Charset

@AgentExecutor("Веб-файл")
@AgentExecutorParameter("URL", "url", AgentExecutorParameterType.STRING,true)
@AgentExecutorParameter("Кодировка", "encoding", AgentExecutorParameterType.STRING)
class WebFileAgentExecutor(context: AgentContext): AbstractAgentExecutor(context) {
    companion object {
        private const val MAX_CHUNK_SIZE = 16384
    }

    override fun execute() {
        val url = URL(context.getParameter("url") ?: throw AgentExecutionException("Не указан URL"))

        val charsetString = context.getParameter("encoding") ?: "UTF-8"
        val charset = try {
            Charset.forName(charsetString)
        } catch (e: Exception) {
            throw AgentExecutionException("Не получилось найти кодировку с названием '$charsetString'")
        }
        val data = removeGarbage(url.readText(charset))

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

    private fun removeGarbage(text: String) = text
        .replace(Regex("[\\r\\n]{2,}"), "\n")
        .replace("\u00a0", " ")
        .replace(Regex("(\\S*?://)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{2,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)"), "")

}