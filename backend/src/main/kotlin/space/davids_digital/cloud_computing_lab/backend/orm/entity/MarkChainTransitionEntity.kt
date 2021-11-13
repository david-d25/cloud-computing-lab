package space.davids_digital.cloud_computing_lab.backend.orm.entity

import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransactionId
import javax.persistence.*

@Entity(name = "mark_chain_transition")
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["beginning", "continuation"])]
)
data class MarkChainTransitionEntity (
    @EmbeddedId
    val id: MarkChainTransactionId? = null,

    @Column(name = "beginning", nullable = true)
    var beginning: String? = null,

    @Column(name = "continuation", nullable = true)
    var continuation: String? = null,

    @Column(name = "transition_count", nullable = false)
    var transitionCount: Long = 0
)