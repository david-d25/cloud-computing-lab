package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
@Qualifier("aws-lambda")
class AwsLambdaModelControlService: ModelControlService {
    override fun applyDataset(agentId: Int, dataId: Long) {
        TODO("Not yet implemented")
    }
}