package space_davids_digital.cloud_computing_lab.lambda.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestHandler

class Handler : RequestHandler<Map<String, String>, String> {
    override fun handleRequest(event: Map<String, String>, context: Context): String {
        val logger: LambdaLogger = context.logger
        logger.log("ENVIRONMENT VARIABLES: " + System.getenv())
        logger.log("CONTEXT: $context")
        return "200 OK"
    }
}