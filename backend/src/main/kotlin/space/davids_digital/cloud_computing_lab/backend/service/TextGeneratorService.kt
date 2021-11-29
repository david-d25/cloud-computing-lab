package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.model.MarkChainTransitionModel
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping.toModel
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import space.davids_digital.cloud_computing_lab.backend.orm.repository.MarkChainTransitionRepository
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer.isTerminator
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer.isWord
import java.util.*

private const val MIN_WORDS = 12
private const val SOFT_MAX_WORDS = 24
private const val HARD_MAX_WORDS = 36

@Service
class TextGeneratorService(
    private val markChainTransitionRepository: MarkChainTransitionRepository,
    private val agentRepository: AgentRepository
) {

    fun generate(text: String, styles: List<Int>): String {
        val result = StringBuilder()

        val effectiveStyles = styles.ifEmpty {
            agentRepository.findIdsBySensitiveAndStatusAndVisible(
                false, AgentStatusEntityEnum.READY, true
            )
        }

        val transitions = mutableMapOf<String?, MutableList<MarkChainTransitionModel>>()
        markChainTransitionRepository.findAllByAgentIds(effectiveStyles)
            .map { it.toModel() }
            .forEach { transitions.computeIfAbsent(it.beginning) { mutableListOf() }.add(it) }

        var wordsGenerated = 0

        while (true) {
            val fullText = text + result.toString()
            val lastWord = pickLastWord(fullText)?.lowercase(Locale.getDefault())
            var newSentenceWord = fullText.isBlank() || fullText.substring(fullText.length).isTerminator()

            var currentTransitions = transitions[lastWord]
            if (currentTransitions == null) {
                currentTransitions = transitions[null] ?: return ""
                result.append(".")
                newSentenceWord = true
            }

            if (wordsGenerated < MIN_WORDS)
                currentTransitions = currentTransitions.filter { it.continuation != null }.toMutableList()

            val divider = currentTransitions.sumOf { it.transitionCount }

            var rVal = Math.random()

            for (i in 0 until currentTransitions.size) {
                rVal -= currentTransitions[i].transitionCount.toDouble() / divider

                if (rVal <= 0.0) {
                    val continuation = currentTransitions[i].continuation
                    if (continuation == null) {
                        result.append(".")

                        if (wordsGenerated > SOFT_MAX_WORDS)
                            break

                    } else {
                        if (result.isNotEmpty() && !pickLastWord(continuation).isTerminator())
                            result.append(" ")

                        result.append(
                            if (newSentenceWord)
                                firstCharUpperCase(continuation)
                            else
                                continuation
                        )
                    }
                    break
                }
            }

            wordsGenerated++
            if (wordsGenerated > HARD_MAX_WORDS)
                break
        }

        return result.toString()
    }

    private fun firstCharUpperCase(text: String): String {
        if (text.isBlank())
            return text
        return text.substring(0, 1).uppercase(Locale.getDefault()) + text.substring(1)
    }

    private fun pickLastWord(text: String): String? {
        if (text.indexOf(" ") != -1)
            return text.substringAfterLast(" ")
        if (text.isNotBlank())
            return text
        return null
    }
}
