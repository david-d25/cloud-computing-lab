package space.davids_digital.cloud_computing_lab.backend.orm.entity

import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "agent")
data class AgentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    var id: Int? = null,

    @Column(name = "type", nullable = false)
    var type: String? = null,

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: AgentStatusEntityEnum? = null,

    @Column(name = "visible", nullable = false)
    var visible: Boolean? = null,

    @Column(name = "update_period_seconds")
    var updatePeriodSeconds: Long? = null,

    @Column(name = "last_update_timestamp")
    var lastUpdateTimestamp: Timestamp? = null,

    @Column(name = "last_applied_data_entry")
    var lastAppliedDataEntry: Long? = null,

    @Column(name = "sensitive", nullable = false)
    var sensitive: Boolean? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_parameter", joinColumns = [
        JoinColumn(name = "agent_id", referencedColumnName = "id")
    ])
    @MapKeyColumn(name = "key")
    @Column(name = "value", columnDefinition = "text")
    var parameters: Map<String, String> = mapOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_memory", joinColumns = [
        JoinColumn(name = "agent_id", referencedColumnName = "id")
    ])
    @MapKeyColumn(name = "key")
    @Column(name = "value", columnDefinition = "text")
    var memory: MutableMap<String, String> = mutableMapOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_data", joinColumns = [
        JoinColumn(name = "agent_id", referencedColumnName = "id")
    ])
    @MapKeyColumn(name = "key")
    @Column(name = "value", columnDefinition = "text")
    var data: MutableMap<Long, String> = mutableMapOf()
)