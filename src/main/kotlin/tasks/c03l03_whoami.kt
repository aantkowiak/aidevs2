package tasks

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import utils.*
import java.io.File
import java.io.IOException
import java.util.ArrayList

fun main() {
    val facts = ArrayList<String>()
    val mainTask = "You have to guess the name of the person that is described by the given facts."
    val rules = File("src/main/resources/whoami_rules.txt").readText()
    val systemPrompt = mainTask + separator + "Rules: $rules" + separator

    runBlocking {
        var answer = "not enough facts"
        while(answer.equals("not enough facts")) {
            try {
                facts.add(fetchHint())
            } catch (e: IOException) {
                delay(10000)
                println(facts)
            }
            answer = extractContent(openAI.chatCompletion(chatCompletionRequest(systemPrompt, buildUserPrompt(facts)))).toString()
        }
        val authorizationToken = authorizeTask("whoami")
        submitAnswer(authorizationToken, asString(answer))
        println("runBlocking - DONE")
    }
    println("DONE")
}

private fun buildUserPrompt(facts: ArrayList<String>) = "Facts: \n ${facts.stream().map { s -> "\n- $s" }.toList()}"

private fun fetchHint(): String {
    val authorizationToken = authorizeTask("whoami")
    val taskJsonString = fetchTask(authorizationToken)
    return extractStringFromJsonString(taskJsonString, "hint")
}