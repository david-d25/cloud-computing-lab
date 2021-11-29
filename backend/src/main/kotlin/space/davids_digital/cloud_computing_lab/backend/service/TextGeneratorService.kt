package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.orm.repository.MarkChainTransitionRepository
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer
import space.davids_digital.cloud_computing_lab.tokenizer.isTerminator
import space.davids_digital.cloud_computing_lab.tokenizer.isWord
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.random.Random

@Service
class TextGeneratorService(
    private val markChainRepo: MarkChainTransitionRepository
){
    /*
        if styles is empty, that means that user chose all agents where sensitive == false

        step 1: get needed agents
        step 2: find a random word that goes after our word
     */
    //TODO: write this function
    fun generate(text: String, styles: List<Int>): String {
        val result = StringBuilder()

        if (styles.isNotEmpty()) {
            //TODO
        }
        result.append(text)

        // all chosen iterated things
        val words = text.split(" ")
        val allTransitions = markChainRepo.findAllByIdAgentId(styles).toList()

        //rng part
        val genSeed = stringToSeed(words)
        val rng = Random(genSeed)
        ////

        var shouldClose = false
        var lastTerminator = "."
        var word = words[words.size-1]
        while (!shouldClose){
            val currentWordTransitions = allTransitions.
                parallelStream().filter { x -> x.beginning == word }.collect(Collectors.toList())
            val howManyInChain: Long = currentWordTransitions.sumOf { x -> x.transitionCount }

            var rVal = rng.nextDouble()

            for (i in 0..currentWordTransitions.size){
                rVal -= currentWordTransitions[i].transitionCount / howManyInChain

                if (rVal <= 0.0){
                    val continuation = currentWordTransitions[i].continuation
                    if (continuation == null){
                        shouldClose = true
                        result.append(lastTerminator)
                    } else {
                        result.append(continuation)
                        word = pickWord(continuation)
                        lastTerminator = pickTerminator(continuation)
                    }

                }
            }
        }
        return result.toString()
    }

    private fun pickTerminator(words:String):String{
        val tokens = Tokenizer.tokenize(words)
        for (i in tokens.size-1..0){
            val last = tokens[i]
            if (last.isTerminator()) return last
        }
        return ""
    }

    private fun pickWord(words: String):String {
        val tokens = Tokenizer.tokenize(words)
        for (i in tokens.size-1..0){
            val last = tokens[i]
            if (last.isWord()) return last
        }
        return ""
    }


    fun stringToSeed(s: List<String>):Long {
        var hash: Long = 0
        hash = s.hashCode().toLong() //FIXME:  change this to something other
        return hash
    }
}
