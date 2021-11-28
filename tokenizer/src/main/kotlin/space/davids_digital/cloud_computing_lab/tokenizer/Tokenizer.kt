package space.davids_digital.cloud_computing_lab.tokenizer

import java.lang.StringBuilder
import java.util.regex.Pattern

object Tokenizer {
    data class Transition (
        val beginning: String?,
        val continuation: String?,
        var count: Int
    )

    private fun String?.isTerminator() = this?.matches(Regex("[.?!]+")) ?: false
    private fun String?.isWord() = this != null && this.matches(Regex("[()\\p{L}+*<>{}\\[\\]!@#\$%^&\"'`~-]+|\\d+"))

    // TODO this works wrong
    fun generateTransitions(text: String, maxWordsPerTransition: Int): Set<Transition> {
        val tokens = tokenize(text)
        val result = mutableMapOf<String?, Transition>()
        fun MutableMap<String?, Transition>.putTransition(beginning: String?, continuation: String?) {
            if (!containsKey(beginning))
                this[beginning] = Transition(beginning, continuation, 1)
            else
                this[beginning]!!.count++
        }

        fun getToken(i: Int) = if (i >= 0 && i < tokens.size) tokens[i] else null
        fun wordsAvailableInSentence(start: Int, max: Int): Int {
            var i = start
            var counter = 0
            while (counter < max) {
                val token = getToken(i)
                if (token == null || token.isTerminator())
                    break
                if (token.isWord())
                    counter++
                i++
            }
            return counter
        }
        fun grabNextNWords(start: Int, nWords: Int): String {
            var i = start
            var counter = 0
            val buffer = StringBuilder()
            while (counter < nWords) {
                val token = getToken(i)
                if (token.isWord()) {
                    buffer.append(" ")
                    counter++
                }
                buffer.append(token)
                i++
            }
            while(getToken(i) != null && !getToken(i).isWord()) {
                buffer.append(getToken(i))
                i++
            }
            return buffer.toString()
        }

        var i = -1
        while (i < tokens.size) {
            val beginning = getToken(i)
            if (beginning.isTerminator()) {
                i++
                continue
            }

            val wordsAvailable = wordsAvailableInSentence(i + 1, maxWordsPerTransition)
            repeat(maxWordsPerTransition) {
                val wordsToUse = it + 1
                if (wordsAvailable >= wordsToUse) {
                    result.putTransition(beginning, grabNextNWords(i, wordsToUse))
                } else if (wordsToUse == 1 && wordsAvailable == 0)
                    result.putTransition(beginning, null)
            }
            i++
        }
        return result.values.toSet()
    }

    private fun tokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        val pattern = Pattern.compile("[\\p{L}-]+|\\d+|\\S+")
        val matcher = pattern.matcher(text)
        while (matcher.find())
            result.add(matcher.group())
        return result
    }
}