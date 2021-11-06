package space.davids_digital.cloud_computing_lab.backend.orm.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity

@Repository
interface AgentRepository: CrudRepository<AgentEntity, Int>