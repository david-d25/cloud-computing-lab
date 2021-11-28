package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransitionId
import java.util.*

@Repository
interface MarkChainTransitionRepository: CrudRepository<MarkChainTransitionEntity, MarkChainTransitionId> {
    fun findByBeginningAndContinuation(beginning: String?, continuation: String?): Optional<MarkChainTransitionEntity>

    @Query("select max(entryId) from mark_chain_transition where agentId = :id")
    fun getMaxEntryIdByAgentId(id: Int): Int?
}