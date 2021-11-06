package space.davids_digital.cloud_computing_lab.backend.orm.entity

import com.sun.istack.NotNull
import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransactionId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "mark_chain_transition")
data class MarkChainTransitionEntity (
    @EmbeddedId
    val id: MarkChainTransactionId? = null,

    @NotNull
    @Column(name = "continuation")
    val continuation: String? = null,

    @NotNull
    @Column(name = "transition_count")
    val transitionCount: Long? = null,

    @NotNull
    @Column(name = "start_count")
    val startCount: Long? = null,

    @NotNull
    @Column(name = "end_count")
    val endCount: Long? = null
)