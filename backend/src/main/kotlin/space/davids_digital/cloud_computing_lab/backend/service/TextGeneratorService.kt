package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import space.davids_digital.cloud_computing_lab.backend.orm.repository.MarkChainTransitionRepository
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer.isTerminator
import java.util.*

private const val MIN_WORDS = 2
private const val SOFT_MAX_WORDS = 10
private const val HARD_MAX_WORDS = 28

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

        var wordsGenerated = 0
        var newSentenceWord = text.isBlank()

        while (true) {
            val fullText = text + result.toString()
            val lastWord = pickLastWord(fullText)?.lowercase(Locale.getDefault())

            var currentTransitions = markChainTransitionRepository.findAllByAgentIdsAndBeginning(effectiveStyles, lastWord)
            if (currentTransitions.isEmpty()) {
                currentTransitions = markChainTransitionRepository.findAllByAgentIdsAndBeginningIsNull(effectiveStyles)
                if (result.isNotBlank() && !result.endsWith(".")) {
                    result.append(".")
                    if (wordsGenerated > SOFT_MAX_WORDS)
                        break
                }
                newSentenceWord = true
            }

            if (wordsGenerated < MIN_WORDS)
                currentTransitions = currentTransitions.filter { it.continuation != null }.toMutableList()

            val divider = currentTransitions.sumOf { it.transitionCount }

            var rVal = Math.random()

            var newWordsAdded = 1;
            for (i in currentTransitions.indices) {
                rVal -= currentTransitions[i].transitionCount.toDouble() / divider

                if (rVal <= 0.0) {
                    val continuation = currentTransitions[i].continuation
                    if (continuation == null) {
                        if (!result.endsWith("."))
                            result.append(".")
                        newSentenceWord = true
                    } else {
                        if (fullText.isNotEmpty() && !pickLastWord(continuation).isTerminator())
                            result.append(" ")

                        result.append(
                            if (newSentenceWord)
                                firstCharUpperCase(continuation)
                            else
                                continuation
                        )

                        newWordsAdded = continuation.split(" ").size
                        newSentenceWord = false
                    }
                    break
                }
            }

            wordsGenerated += newWordsAdded
            if (newSentenceWord && wordsGenerated > SOFT_MAX_WORDS || wordsGenerated > HARD_MAX_WORDS)
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
        if (text.trim().indexOf(" ") != -1)
            return text.trim().substringAfterLast(" ")
        if (text.isNotBlank())
            return text
        return null
    }
}
