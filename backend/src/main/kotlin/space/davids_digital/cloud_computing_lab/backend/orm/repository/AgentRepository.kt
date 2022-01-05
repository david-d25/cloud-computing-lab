package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import java.util.*
import javax.transaction.Transactional

@Repository
interface AgentRepository: CrudRepository<AgentEntity, Int> {
    @Query("select distinct a.id from agent a join a.data data where size(data) > 0 and (" +
            "a.lastAppliedDataEntry is null or " +
            "a.lastAppliedDataEntry < size(data) - 1)")
    fun getAgentNeedingModelUpdateIds(): List<Int>

    @Query("select max(size(data)) from agent a join a.data data where a.id = :id")
    fun getAgentDataEntriesSize(@Param("id") id: Int): Long

    @Query("select lastAppliedDataEntry from agent where id = :id")
    fun getAgentLastAppliedDataEntryId(@Param("id") id: Int): Long?

    @Query("select id from agent where sensitive = :sensitive and status = :status and visible = :visible")
    fun findIdsBySensitiveAndStatusAndVisible(sensitive: Boolean, status: AgentStatusEntityEnum, visible: Boolean): List<Int>

    @Query("select value(a.parameters) from agent a join a.parameters p where a.id = :id and :key = key(p)")
    fun getParameterByKey(@Param("id") id: Int, @Param("key") key: String): String?

    @Query("select value(a.memory) from agent a join a.memory m where a.id = :id and :key = key(m)")
    fun getMemoryByKey(@Param("id") id: Int, @Param("key") key: String): String?

    @Modifying
    @Query("insert into agent_memory (agent_id, value, key) values (:id, :value, :key) on conflict do update set key = :key", nativeQuery = true)
    fun setMemoryByKey(@Param("id") id: Int, @Param("key") key: String, @Param("value") value: String)

    @Modifying
    @Query("insert into agent_data (agent_id, value, key) values (:id, :data, coalesce((select max(key) from agent_data where agent_id = :id), -1) + 1)", nativeQuery = true)
    fun addData(@Param("id") id: Int, @Param("data") data: String)

    @Modifying
    @Query("delete from agent_data where agent_id = :id", nativeQuery = true)
    fun clearData(@Param("id") id: Int)

    @Modifying
    @Query("delete from mark_chain_transition where agent_id = :id", nativeQuery = true)
    fun clearTransitions(@Param("id") id: Int)

    @Modifying
    @Query("update agent set lastAppliedDataEntry = null where id = :id")
    fun resetLastAppliedEntry(@Param("id") id: Int)
}