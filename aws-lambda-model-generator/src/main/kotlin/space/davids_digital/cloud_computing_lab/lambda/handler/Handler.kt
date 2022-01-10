package space.davids_digital.cloud_computing_lab.lambda.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestHandler
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.math.floor


class Handler : RequestHandler<Map<String, String>, String> {
    companion object {
        const val DB_URL = "db_url"
        const val DB_USERNAME = "db_username"
        const val DB_PASSWORD = "db_password"
        const val AGENT_ID = "agent_id"
        const val DATA_ID = "data_id"
        const val MAX_WORDS_PER_TRANSITION_BIAS = "max_words_per_transition_bias"
        const val MIN_WORDS_PER_TRANSITION = "min_words_per_transition"
    }

    override fun handleRequest(event: Map<String, String>, context: Context): String {
        val logger: LambdaLogger = context.logger
        logger.log("Hello!")

        try {
            fun getParamSafe(key: String) = event[key] ?: throw RuntimeException("$key not specified")

            val agentId = getParamSafe(AGENT_ID).toInt()
            val dataId = getParamSafe(DATA_ID).toLong()

            val maxWordsPerTransitionBias = getParamSafe(MAX_WORDS_PER_TRANSITION_BIAS).toInt()
            val minWordsPerTransition = getParamSafe(MIN_WORDS_PER_TRANSITION).toInt()

            logger.log("Trying to connect...")
            val props = Properties()
            props.setProperty("user", getParamSafe(DB_USERNAME))
            props.setProperty("password", getParamSafe(DB_PASSWORD))
            props.setProperty("ssl", "true")
            val connection = DriverManager.getConnection(getParamSafe(DB_URL), props)
            logger.log("Connected")

            val data = connection.getData(agentId, dataId) ?: throw RuntimeException("Data id $dataId not found in agent id $agentId")

            val maxWordsPerTransition = maxWordsPerTransitionBias + floor(connection.getRecommendedMaxWordsPerTransition(agentId)).toInt()
            val transitions = Tokenizer.generateTransitions(data, minWordsPerTransition, maxWordsPerTransition)

            // start transaction
            connection.autoCommit = false
            try {
                transitions.stream()
                    .filter {
                        (it.beginning == null || it.beginning!!.length < 255) && (it.continuation == null || it.continuation!!.length < 255) }
                    .forEach {
                        if (connection.existsByAgentIdAndBeginningAndContinuation(agentId, it.beginning, it.continuation))
                            connection.updateExistingTransition(agentId, it.beginning, it.continuation, it.count.toLong())
                        else
                            connection.putNewTransition(agentId, it.beginning, it.continuation, it.count.toLong())
                    }

                connection.updateAgentLastAppliedDataEntry(agentId, dataId)

                connection.commit()
            } catch (e: Exception) {
                connection.rollback()
                throw RuntimeException(e)
            }

            return "200 OK"
        } catch (e: RuntimeException) {
            return "500 Internal Server Error\n\n{\"error\": 1, \"error_message:\": \"${e.message}\"}"
        }
    }

    private fun Connection.getData(agentId: Int, dataId: Long) =
        prepareStatement("select value from agent_data where agent_id = ? and key = ?").let {
            it.setInt(1, agentId)
            it.setLong(2, dataId)
            it.executeQuery().getString(1)
        }

    private fun Connection.getRecommendedMaxWordsPerTransition(agentId: Int) =
        prepareStatement("select coalesce(sum(transition_count)/count(entry_id), 1) from mark_chain_transition where agent_id = ?").let {
            it.setInt(1, agentId)
            it.executeQuery().getDouble(1)
        }

    private fun Connection.existsByAgentIdAndBeginningAndContinuation(agentId: Int, beginning: String?, continuation: String?) =
        prepareStatement("select exists(select * from mark_chain_transition where agent_id = ? and ((? is null and beginning is null) or (beginning = cast(? as text))) and ((? is null and continuation is null) or (continuation = cast(? as text))))").let {
            it.setInt(1, agentId)
            it.setString(2, beginning)
            it.setString(3, beginning)
            it.setString(4, continuation)
            it.setString(5, continuation)
            it.executeQuery().getBoolean(1)
        }

    private fun Connection.putNewTransition(agentId: Int, beginning: String?, continuation: String?, count: Long) =
        prepareStatement("insert into mark_chain_transition (agent_id, entry_id, beginning, continuation, transition_count) values (?, (select coalesce(max(entry_id), -1) + 1 from mark_chain_transition where agent_id = ?), ?, ?, ?)").let {
            it.setInt(1, agentId)
            it.setInt(2, agentId)
            it.setString(3, beginning)
            it.setString(4, continuation)
            it.setLong(5, count)
            it.executeUpdate()
        }

    private fun Connection.updateExistingTransition(agentId: Int, beginning: String?, continuation: String?, countToAdd: Long) =
        prepareStatement("update mark_chain_transition set transition_count = mark_chain_transition.transition_count + ? where agent_id = ? and ((? is null and beginning is null) or (beginning = cast(? as text))) and ((? is null and continuation is null) or (continuation = cast(? as text)))").let {
            it.setLong(1, countToAdd)
            it.setInt(2, agentId)
            it.setString(3, beginning)
            it.setString(4, beginning)
            it.setString(5, continuation)
            it.setString(6, continuation)
            it.executeUpdate()
        }

    private fun Connection.updateAgentLastAppliedDataEntry(agentId: Int, value: Long) =
        prepareStatement("update agent set last_applied_data_entry = ? where id = ?").let {
            it.setLong(1, value)
            it.setInt(2, agentId)
            it.executeUpdate()
        }
}