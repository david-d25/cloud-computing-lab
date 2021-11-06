package space.davids_digital.cloud_computing_lab.backend.orm.entity.id

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
data class MarkChainTransactionId (
    @Column(name = "agent_id")
    var agentId: Int? = null,

    @Column(name = "entry")
    var entry: Int? = null
): Serializable