package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransitionId

@Repository
interface MarkChainTransitionRepository: CrudRepository<MarkChainTransitionEntity, MarkChainTransitionId> {
    @Query("from mark_chain_transition where agentId in :agentIds and (beginning is null and :beginning is null or beginning = cast(:beginning as text))")
    fun findAllByAgentIdsAndBeginning(agentIds: List<Int>, beginning: String?): List<MarkChainTransitionEntity>

    @Query("from mark_chain_transition where agentId in :agentIds and beginning is null")
    fun findAllByAgentIdsAndBeginningIsNull(agentIds: List<Int>): List<MarkChainTransitionEntity>

    fun existsByAgentIdAndBeginningAndContinuation(agentId: Int, beginning: String?, continuation: String?): Boolean

    @Query("select coalesce(sum(transition_count)/count(entry_id), 1) from mark_chain_transition where agent_id = :agentId", nativeQuery = true)
    fun getRecommendedMaxWordsPerTransition(agentId: Int): Double

    @Modifying
    @Query("insert into mark_chain_transition (agent_id, entry_id, beginning, continuation, transition_count) values (:agentId, (select coalesce(max(entry_id), -1) + 1 from mark_chain_transition where agent_id = :agentId), :beginning, :continuation, :count)", nativeQuery = true)
    fun putNewTransition(agentId: Int, beginning: String?, continuation: String?, count: Long)

    @Modifying
    @Query("update mark_chain_transition set transition_count = mark_chain_transition.transition_count + :countToAdd where agent_id = :agentId and ((:beginning is null and beginning is null) or (beginning = cast(:beginning as text))) and ((:continuation is null and continuation is null) or (continuation = cast(:continuation as text)))", nativeQuery = true)
    fun updateExistingTransition(agentId: Int, beginning: String?, continuation: String?, countToAdd: Long)

    @Query("from mark_chain_transition where agentId in :ids")
    fun findAllByAgentIds(ids: Iterable<Int>): Iterable<MarkChainTransitionEntity>

    fun removeByAgentId(id: Int)
}