package space.davids_digital.cloud_computing_lab.backend.orm.entity.id

import java.io.Serializable
import javax.persistence.*

@Embeddable
data class MarkChainTransitionId (
    @Column(name = "agent_id", nullable = false)
    var agentId: Int? = null,

    @Column(name = "entry_id", nullable = false)
    var entryId: Int? = null
): Serializable