package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransitionId

@Repository
interface MarkChainTransitionRepository: CrudRepository<MarkChainTransitionEntity, MarkChainTransitionId> {
    @Query("from mark_chain_transition where agentId in :agentIds and beginning = :beginning")
    fun findAllByAgentIdsAndBeginning(agentIds: List<Int>, beginning: String?): List<MarkChainTransitionEntity>

    @Query("from mark_chain_transition where agentId in :agentIds and beginning is null")
    fun findAllByAgentIdsAndBeginningIsNull(agentIds: List<Int>): List<MarkChainTransitionEntity>

    @Query("select max(entryId) from mark_chain_transition where agentId = :id")
    fun getMaxEntryIdByAgentId(id: Int): Int?

    @Query("from mark_chain_transition where agentId in :ids")
    fun findAllByAgentIds(ids: Iterable<Int>): Iterable<MarkChainTransitionEntity>

    fun removeByAgentId(id: Int)
}