package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity
import javax.transaction.Transactional

@Repository
interface AgentRepository: CrudRepository<AgentEntity, Int> {
    @Query("select a.id from agent a join a.data data where size(data) > 0 and (" +
            "a.lastAppliedDataEntry is null or " +
            "a.lastAppliedDataEntry < size(data) - 1)")
    fun getAgentNeedingModelUpdateIds(): List<Int>

    @Query("select size(data) from agent a join a.data data where a.id = :id")
    fun getAgentDataEntriesSize(@Param("id") id: Int): Long

    @Query("select lastAppliedDataEntry from agent where id = :id")
    fun getAgentLastAppliedDataEntryId(@Param("id") id: Int): Long?
}