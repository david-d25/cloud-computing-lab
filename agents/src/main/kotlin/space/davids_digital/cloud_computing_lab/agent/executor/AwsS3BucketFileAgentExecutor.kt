package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.AgentParameterType
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException

@AgentExecutor("Amazon S3 File")
class AwsS3BucketFileAgentExecutor(context: AgentContext): AbstractAgentExecutor(context) {
    override fun execute() {
        val s3Bucket = context.getParameter("s3_bucket") ?: throw AgentExecutionException("Please, provide 's3_bucket' parameter")

        TODO()
//        context.clearData()
//        context.submitData(fileContents)
    }
}