package space.davids_digital.cloud_computing_lab.backend.orm.entity

import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransitionId
import java.io.Serializable
import javax.persistence.*

@Entity(name = "mark_chain_transition")
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["agent_id", "beginning", "continuation"])]
)
@IdClass(MarkChainTransitionId::class)
data class MarkChainTransitionEntity (
    @Id
    @Column(name = "agent_id", nullable = false)
    var agentId: Int? = null,

    @Column(name = "beginning", nullable = true)
    var beginning: String? = null,

    @Column(name = "continuation", nullable = true)
    var continuation: String? = null,

    @Column(name = "transition_count", nullable = false)
    var transitionCount: Long = 0,
): Serializable