package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import space.davids_digital.cloud_computing_lab.agent.AgentConstants.Companion.ERROR_MESSAGE
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException
import space.davids_digital.cloud_computing_lab.agent.executor.AbstractAgentExecutor
import space.davids_digital.cloud_computing_lab.backend.agent.context.EntityBasedAgentContext
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import java.lang.Exception
import java.sql.Timestamp
import java.time.Instant
import java.util.concurrent.Executors
import javax.persistence.EntityManagerFactory

@Service
@Qualifier("local-classpath")
class LocalClasspathAgentExecutionService @Autowired constructor(
    private val agentRepository: AgentRepository,
    private val entityManagerFactory: EntityManagerFactory
): AgentExecutionService {
    private val executorService = Executors.newCachedThreadPool()

    override fun enqueueExecution(id: Int) {
        var entity = agentRepository.findById(id).orElseThrow { ServiceException("Agent id $id not found") }
        try {
            val executorClass: Class<*> = Class.forName(entity.type)
            if (executorClass is AbstractAgentExecutor) {
                val context = EntityBasedAgentContext(entity)
                val constructor = executorClass.getConstructor(AgentContext::class.java)
                val executor = constructor.newInstance(context) as AbstractAgentExecutor

                executorService.execute {
                    val entityManager = entityManagerFactory.createEntityManager()
                    val transaction = entityManager.transaction
                    try {
                        entity.lastUpdateTimestamp = Timestamp.from(Instant.now())
                        entity.status = AgentStatusEntityEnum.RUNNING
                        entity = agentRepository.save(entity)

                        transaction.begin()
                        executor.execute()
                        entity = agentRepository.save(entity)
                        transaction.commit()

                        entity.status = AgentStatusEntityEnum.SLEEPING
                        entity = agentRepository.save(entity)
                    } catch (e: AgentExecutionException) {
                        transaction.rollback()
                        entity.status = AgentStatusEntityEnum.ERROR
                        entity.memory[ERROR_MESSAGE] = e.message
                        entity = agentRepository.save(entity)
                    } catch (e: Exception) {
                        transaction.rollback()
                        entity.status = AgentStatusEntityEnum.ERROR
                        entity.memory[ERROR_MESSAGE] = "Unknown agent execution error"
                        entity = agentRepository.save(entity)
                    } finally {
                        entityManager.close()
                    }
                }
            } else {
                entity.status = AgentStatusEntityEnum.ERROR
                entity.memory[ERROR_MESSAGE] = "Agent id '${id}' has wrong type"
            }
        } catch (e: ClassNotFoundException) {
            entity.status = AgentStatusEntityEnum.ERROR
            entity.memory[ERROR_MESSAGE] = "Agent of type '${entity.type}' not found"
        }
        agentRepository.save(entity)
    }
}