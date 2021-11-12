package space.davids_digital.cloud_computing_lab.backend.orm.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "server_config")
data class ServerConfigEntity(
    @Id
    @Column(name = "name")
    val name: String? = null,

    @Column(name = "value", nullable = false)
    val value: String? = null
)