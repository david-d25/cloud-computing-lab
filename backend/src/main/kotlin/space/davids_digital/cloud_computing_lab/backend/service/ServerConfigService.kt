package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.davids_digital.cloud_computing_lab.backend.orm.entity.ServerConfigEntity
import space.davids_digital.cloud_computing_lab.backend.orm.repository.ServerConfigRepository

@Service
class ServerConfigService @Autowired constructor(
    private val configRepository: ServerConfigRepository
) {
    fun getConfig(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        configRepository.findAll().forEach { v -> result[v.name!!] = v.value!! }
        return result
    }

    @Transactional
    fun setConfig(config: Map<String, String>) {
        configRepository.deleteAll()
        configRepository.saveAll(config.entries.map { ServerConfigEntity(it.key, it.value) })
        println(configRepository.findAll())
    }
}