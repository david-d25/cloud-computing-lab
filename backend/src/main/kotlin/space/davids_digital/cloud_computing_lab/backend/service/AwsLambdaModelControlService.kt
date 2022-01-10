package space.davids_digital.cloud_computing_lab.backend.service

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClientBuilder
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.model.InvokeResult
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.util.GlobalConstraints.MAX_WORDS_PER_TRANSITION_BIAS
import space.davids_digital.cloud_computing_lab.backend.util.GlobalConstraints.MIN_WORDS_PER_TRANSITION
import java.nio.charset.StandardCharsets

@Service
@Qualifier("aws-lambda")
class AwsLambdaModelControlService(
    private val objectMapper: ObjectMapper,
    private val environment: Environment
): ModelControlService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun applyDataset(agentId: Int, dataId: Long) {
        val functionName = "lambda-model-generator"
        val invokeRequest = InvokeRequest()
            .withFunctionName(functionName)
            .withPayload(objectMapper.writeValueAsString(RequestPayload(
                dbUrl = environment["DB_URL"],
                dbUsername = environment["DB_USERNAME"],
                dbPassword = environment["DB_PASSWORD"],
                agentId = agentId,
                dataId = dataId,
                maxWordsPerTransitionBias = MAX_WORDS_PER_TRANSITION_BIAS,
                minWordsPerTransition = MIN_WORDS_PER_TRANSITION
            )))

        lateinit var invokeResult: InvokeResult

        logger.info("Invoking AWS Lambda function to apply data id $dataId for agent id $agentId")
        try {
            val awsLambda = AWSLambdaClientBuilder.standard()
                .withCredentials(ProfileCredentialsProvider())
                .withRegion(Regions.US_EAST_2).build()
            invokeResult = awsLambda.invoke(invokeRequest)
            val responseBody = String(invokeResult.payload.array(), StandardCharsets.UTF_8)
            if (invokeResult.statusCode == 200) {
                val response = objectMapper.readValue(responseBody, ResponsePayload::class.java)
                if (response.error == 1)
                    throw ServiceException("Failed to AWS Lambda function, message: ${response.errorMessage}")
                else
                    logger.info("AWS Lambda function done")
            } else {
                throw ServiceException("Failed to AWS Lambda function, status code is ${invokeResult.statusCode}, response: $responseBody")
            }
        } catch (e: Exception) {
            throw ServiceException("Failed to invoke AWS Lambda function: ${e.message}")
        }
    }

    private class ResponsePayload {
        val error: Int? = null

        @JsonProperty("error_message")
        val errorMessage: String? = null
    }

    private data class RequestPayload @JvmOverloads constructor(
        @get:JsonProperty("db_url")
        val dbUrl: String? = null,

        @get:JsonProperty("db_username")
        val dbUsername: String? = null,

        @get:JsonProperty("db_password")
        val dbPassword: String? = null,

        @get:JsonProperty("agent_id")
        val agentId: Int? = null,

        @get:JsonProperty("data_id")
        val dataId: Long? = null,

        @get:JsonProperty("max_words_per_transition_bias")
        val maxWordsPerTransitionBias: Int? = null,

        @get:JsonProperty("min_words_per_transition")
        val minWordsPerTransition: Int? = null
    )
}