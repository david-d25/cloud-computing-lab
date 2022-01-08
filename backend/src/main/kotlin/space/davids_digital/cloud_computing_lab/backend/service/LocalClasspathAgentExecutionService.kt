package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import space.davids_digital.cloud_computing_lab.agent.AgentConstants.Companion.ERROR_MESSAGE
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException
import space.davids_digital.cloud_computing_lab.agent.executor.AbstractAgentExecutor
import space.davids_digital.cloud_computing_lab.backend.agent.context.RepositoryBasedAgentContext
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import java.sql.Timestamp
import java.time.Instant
import java.util.concurrent.Executors

@Service
@Qualifier("local-classpath")
class LocalClasspathAgentExecutionService @Autowired constructor(
    private val agentRepository: AgentRepository,
    private val transactionManager: PlatformTransactionManager,
    private val serverConfigService: ServerConfigService
): AgentExecutionService {
    private val executorService = Executors.newCachedThreadPool()

    private fun getAgentSafe(id: Int) = agentRepository.findById(id).orElseThrow { ServiceException("Agent id $id not found") }

    override fun enqueueExecution(id: Int) {
        try {
            val executorClass: Class<*> = Class.forName(getAgentSafe(id).type)
            if (AbstractAgentExecutor::class.java.isAssignableFrom(executorClass)) {
                val context = RepositoryBasedAgentContext(id, agentRepository, serverConfigService)
                val constructor = executorClass.getConstructor(AgentContext::class.java)
                val executor = constructor.newInstance(context) as AbstractAgentExecutor

                executorService.execute {
                    lateinit var status: TransactionStatus
                    try {
                        getAgentSafe(id).let {
                            it.lastUpdateTimestamp = Timestamp.from(Instant.now())
                            it.status = AgentStatusEntityEnum.RUNNING
                            agentRepository.save(it)
                        }

                        status = transactionManager.getTransaction(null)
                        executor.execute()
                        getAgentSafe(id).let {
                            it.status = AgentStatusEntityEnum.READY
                            agentRepository.save(it)
                        }
                        transactionManager.commit(status)
                    } catch (e: AgentExecutionException) {
                        transactionManager.rollback(status)
                        getAgentSafe(id).let {
                            it.status = AgentStatusEntityEnum.ERROR
                            it.memory[ERROR_MESSAGE] = e.message
                            agentRepository.save(it)
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        transactionManager.rollback(status)
                        getAgentSafe(id).let {
                            it.status = AgentStatusEntityEnum.ERROR
                            it.memory[ERROR_MESSAGE] = "В агенте произошла ошибка, но сервер не сообщил, какая именно"
                            agentRepository.save(it)
                        }
                    }
                }
            } else {
                getAgentSafe(id).let {
                    it.status = AgentStatusEntityEnum.ERROR
                    it.memory[ERROR_MESSAGE] = "У агента с id $id неправильный тип"
                    agentRepository.save(it)
                }
            }
        } catch (e: ClassNotFoundException) {
            getAgentSafe(id).let {
                it.status = AgentStatusEntityEnum.ERROR
                it.memory[ERROR_MESSAGE] = "В сервере не нашелся агент с типом '${it.type}'"
                agentRepository.save(it)
            }
        }
    }
}