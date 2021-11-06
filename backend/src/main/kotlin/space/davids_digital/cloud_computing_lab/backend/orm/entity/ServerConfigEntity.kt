package space.davids_digital.cloud_computing_lab.backend.orm.entity

import com.sun.istack.NotNull
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "server_config")
data class ServerConfigEntity(
    @Id
    @Column(name = "name")
    val name: String? = null,

    @NotNull
    @Column(name = "value")
    val value: String? = null
)
