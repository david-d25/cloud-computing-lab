package space.davids_digital.cloud_computing_lab.agent.executor

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutorParameter
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException
import java.net.URL

@AgentExecutor("Группа VK")
@AgentExecutorParameter("URL группы", "community_domain", required = true)
@AgentExecutorParameter("Сервисный ключ доступа", "service_token", required = true)
class VkGroupAgentExecutor(context: AgentContext) : AbstractAgentExecutor(context) {
    companion object {
        const val PROTOCOL = "https"
        const val API_DOMAIN = "api.vk.com"
        const val METHOD_NAME = "wall.get"
        const val MAX_POSTS_PER_REQUEST = 100
        const val API_VERSION = 5.131

        const val POSTS_PROCESSED = "posts_processed"
    }

    // https://vk.com/dev/wall.get
    override fun execute() {
        val communityDomain = context.getParameter("community_domain")?.substringAfterLast("/")
            ?: throw AgentExecutionException("Не указан URL группы")

        val token = context.getParameter("service_token")
            ?: throw AgentExecutionException("Не указан ключ доступа")

        var postsProcessed = (context.getMemoryEntry(POSTS_PROCESSED) ?: "0").toInt()
        val postsCount = callSafe(token, communityDomain, 1).count!!
        val postsRemainingToProcess = (postsCount - postsProcessed).coerceAtMost(MAX_POSTS_PER_REQUEST)

        if (postsRemainingToProcess == 0)
            return

        val offset = postsCount - postsProcessed - postsRemainingToProcess
        val response = callSafe(
            token,
            communityDomain,
            postsRemainingToProcess,
            offset
        )

        response.items.forEach {
            if (it.text.isNotBlank())
                context.submitData(removeGarbage(it.text))
            postsProcessed++
        }

        context.setMemoryEntry(POSTS_PROCESSED, postsProcessed.toString())
    }

    private fun removeGarbage(text: String) = text
        .replace(Regex("[\\r\\n]{2,}"), "")
        .replace("\u00a0", " ")
        .replace(Regex("(\\S*?://)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{2,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)"), "")

    private fun callSafe(token: String, communityDomain: String, count: Int, offset: Int? = 0): Response {
        val url = URL(
            "$PROTOCOL://$API_DOMAIN/method/$METHOD_NAME" +
                    "?v=$API_VERSION" +
                    "&access_token=$token" +
                    "&domain=$communityDomain" +
                    "&offset=$offset" +
                    "&count=$count"
        )

        val wall = ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(url, Wall::class.java)

        if (wall.response == null) {
            if (wall.error != null) {
                if (wall.error.errorCode == 100)
                    throw AgentExecutionException("Нет доступа к группе. Возможно, группа закрыта или удалена")
                throw AgentExecutionException("Ошибка API: '${wall.error.errorMsg}'")
            }
            throw AgentExecutionException("Пустой ответ от $API_DOMAIN")
        }

        return wall.response
    }

    private class Wall @JvmOverloads constructor(
        val error: Error? = null,
        val response: Response? = null
    )

    private class Response @JvmOverloads constructor(
        val count: Int? = null,
        val items: List<WallItem> = emptyList()
    )

    private class WallItem @JvmOverloads constructor(
        val id: Int? = null,
        val text: String = ""
    )

    private class Error {
        @JsonProperty("error_code") val errorCode: Int? = null
        @JsonProperty("error_msg")  val errorMsg: String? = null
    }
}