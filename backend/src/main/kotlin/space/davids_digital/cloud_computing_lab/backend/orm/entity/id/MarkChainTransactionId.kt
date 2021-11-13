package space.davids_digital.cloud_computing_lab.backend.orm.entity.id

import java.io.Serializable
import javax.persistence.*

@Embeddable
data class MarkChainTransactionId (
    @Column(name = "agent_id")
    var agentId: Int? = null,

    @Column(name = "entry_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var entryId: Int? = null
): Serializable