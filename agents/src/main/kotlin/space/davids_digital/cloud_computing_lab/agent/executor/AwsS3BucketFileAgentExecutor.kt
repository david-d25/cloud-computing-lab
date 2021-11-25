package space.davids_digital.cloud_computing_lab.agent.executor

import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutorParameter
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException

@AgentExecutor("Файл Amazon S3")
@AgentExecutorParameter("Имя бакета", "s3_bucket", AgentExecutorParameterType.STRING,true)
// TODO other S3/Amazon parameters
class AwsS3BucketFileAgentExecutor(context: AgentContext): AbstractAgentExecutor(context) {
    override fun execute() {
        val s3Bucket = context.getParameter("s3_bucket") ?: throw AgentExecutionException("Please, provide 's3_bucket' parameter")

        TODO()
//        context.clearData()
//        context.submitData(fileContents)
    }
}