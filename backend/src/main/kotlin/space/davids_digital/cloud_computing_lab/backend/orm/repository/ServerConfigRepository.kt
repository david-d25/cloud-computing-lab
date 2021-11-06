package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.ServerConfigEntity

@Repository
interface ServerConfigRepository: CrudRepository<ServerConfigEntity, String>