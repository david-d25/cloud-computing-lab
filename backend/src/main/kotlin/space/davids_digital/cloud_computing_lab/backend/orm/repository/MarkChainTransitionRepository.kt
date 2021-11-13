package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransactionId
import java.util.*

@Repository
interface MarkChainTransitionRepository: CrudRepository<MarkChainTransitionEntity, MarkChainTransactionId> {
    fun findByBeginningAndContinuation(beginning: String?, continuation: String?): Optional<MarkChainTransitionEntity>
}