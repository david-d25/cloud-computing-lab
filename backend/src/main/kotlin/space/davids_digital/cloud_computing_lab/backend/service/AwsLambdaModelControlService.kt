package space.davids_digital.cloud_computing_lab.backend.service

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
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
import java.nio.charset.StandardCharsets

@Service
@Qualifier("aws-lambda")
class AwsLambdaModelControlService(
    private val objectMapper: ObjectMapper,
    private val environment: Environment,
    private val configService: ServerConfigService
): ModelControlService {
    companion object {
        const val AWS_ACCESS_KEY = "AWS_ACCESS_KEY"
        const val AWS_SECRET_KEY = "AWS_SECRET_KEY"
    }
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun applyDataset(agentId: Int, dataId: Long) {
        val accessKey = configService.getConfig(AWS_ACCESS_KEY)
        val secretKey = configService.getConfig(AWS_SECRET_KEY)

        if (accessKey == null) {
            logger.error("'$AWS_ACCESS_KEY' config is not provided, dataset won't be applied")
            return
        }

        if (secretKey == null) {
            logger.error("'$AWS_SECRET_KEY' config is not provided, dataset won't be applied")
            return
        }

        val functionName = "lambda-model-generator"
        val invokeRequest = InvokeRequest()
            .withFunctionName(functionName)
            .withPayload(objectMapper.writeValueAsString(RequestPayload(
                dbUrl = environment["DB_URL"],
                dbUsername = environment["DB_USERNAME"],
                dbPassword = environment["DB_PASSWORD"],
                agentId = agentId,
                dataId = dataId
            )))

        lateinit var invokeResult: InvokeResult

        logger.info("Invoking AWS Lambda function to apply data id $dataId for agent id $agentId")
        try {
            val awsLambda = AWSLambdaClientBuilder.standard()
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
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
        val dataId: Long? = null
    )
}