package space.davids_digital.cloud_computing_lab.backend.orm.entity

import com.sun.istack.NotNull
import org.hibernate.type.BlobType
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "agent")
data class AgentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    val id: Int? = null,

    @NotNull
    @Column(name = "type")
    val type: String? = null,

    @NotNull
    @Column(name = "name")
    val name: String? = null,

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: AgentStatusEntityEnum? = null,

    @NotNull
    @Column(name = "visible")
    val visible: Boolean? = null,

    @Column(name = "update_period_seconds")
    val updatePeriodSeconds: Long? = null,

    @Column(name = "last_update_timestamp")
    var lastUpdateTimestamp: Timestamp? = null,

    @NotNull
    @Column(name = "sensitive")
    val sensitive: Boolean? = null,

    @ElementCollection
    @CollectionTable(name = "agent_parameter", joinColumns = [
        JoinColumn(name = "agent_id", referencedColumnName = "id")
    ])
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    val parameters: Map<String, ByteArray> = mapOf(),

    @ElementCollection
    @CollectionTable(name = "agent_memory", joinColumns = [
        JoinColumn(name = "agent_id", referencedColumnName = "id")
    ])
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    val memory: MutableMap<String, String> = mutableMapOf(),

    @ElementCollection
    @CollectionTable(name = "agent_data", joinColumns = [
        JoinColumn(name = "agent_id", referencedColumnName = "id")
    ])
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    val data: MutableMap<Long, String> = mutableMapOf(),
)