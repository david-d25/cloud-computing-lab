package space.davids_digital.cloud_computing_lab.tokenizer

import java.lang.StringBuilder
import java.util.*
import java.util.regex.Pattern

object Tokenizer {
    data class Transition (
        val beginning: String,
        val continuation: String,
        var count: Int
    )

    fun String.isTerminator() = this.matches(Regex("[.?!]*"))
    fun String.isWord() = this.matches(Regex("[()\\p{L}+*<>{}\\[\\]!@#\$%^&\"'`~-]+|\\d+"))

    fun generateTransitions(text: String, minWordsPerTransition: Int, maxWordsPerTransition: Int): Set<Transition> {
        val tokens = tokenize(text)
        val result = mutableMapOf<Pair<String, String>, Transition>()
        fun MutableMap<Pair<String, String>, Transition>.putTransition(beginning: String, continuation: String) {
            val key = Pair(beginning, continuation)
            if (!containsKey(key))
                this[key] = Transition(beginning, continuation, 1)
            else
                this[key]!!.count++
        }

        fun getToken(i: Int) = if (i >= 0 && i < tokens.size) tokens[i] else ""
        fun wordsAvailableInSentence(start: Int, max: Int): Int {
            var i = start
            var counter = 0
            while (counter < max) {
                val token = getToken(i)
                if (token == "" || token.isTerminator())
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
                    if (counter > 0)
                        buffer.append(" ")
                    counter++
                }
                buffer.append(token)
                i++
            }
            while(getToken(i) != "" && !getToken(i).isWord()) {
                buffer.append(getToken(i))
                i++
            }
            return buffer.toString()
        }

        var currentTokenIndex = -1
        while (currentTokenIndex < tokens.size) {
            var beginning = getToken(currentTokenIndex).lowercase(Locale.getDefault())
            if (beginning.isTerminator()) {
                currentTokenIndex++
                continue
            }

            if (beginning != "") {
                while (getToken(currentTokenIndex + 1) != "" && !getToken(currentTokenIndex + 1).isWord()) {
                    currentTokenIndex++
                    beginning += getToken(currentTokenIndex)
                }
            }

            val wordsAvailable = wordsAvailableInSentence(currentTokenIndex + 1, maxWordsPerTransition)
            for (wordsToUse in minWordsPerTransition .. maxWordsPerTransition) {
                if (wordsAvailable >= wordsToUse) {
                    result.putTransition(beginning, grabNextNWords(currentTokenIndex + 1, wordsToUse))
                } else if (wordsToUse == 1 && wordsAvailable == 0)
                    result.putTransition(beginning, "")
            }

            currentTokenIndex++

            while (getToken(currentTokenIndex) != "" && !getToken(currentTokenIndex).isWord())
                currentTokenIndex++
        }
        return result.values.toSet()
    }

    private fun tokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        val pattern = Pattern.compile("(\\n\\s*)?([\\p{L}-]+|\\d+|\\S+)")
        val matcher = pattern.matcher(text)
        while (matcher.find() && matcher.group().isNotBlank())
            result.add(matcher.group())
        return result
    }
}