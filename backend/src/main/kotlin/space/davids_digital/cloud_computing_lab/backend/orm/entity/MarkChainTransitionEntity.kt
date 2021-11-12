package space.davids_digital.cloud_computing_lab.backend.orm.entity

import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransactionId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "mark_chain_transition")
data class MarkChainTransitionEntity (
    @EmbeddedId
    val id: MarkChainTransactionId? = null,

    @Column(name = "continuation", nullable = false)
    val continuation: String? = null,

    @Column(name = "transition_count", nullable = false)
    val transitionCount: Long? = null,

    @Column(name = "start_count", nullable = false)
    val startCount: Long? = null,

    @Column(name = "end_count", nullable = false)
    val endCount: Long? = null
)