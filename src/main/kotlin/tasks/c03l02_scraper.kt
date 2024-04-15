package tasks
import kotlinx.coroutines.runBlocking
import utils.*


fun main() {

    val authorizationToken = authorizeTask("scraper")

    val taskJsonString = fetchTask(authorizationToken)
    val taskMessage = extractStringFromJsonString(taskJsonString, "msg")
    val taskInput = extractStringFromJsonString(taskJsonString, "input")
    val taskQuestion = extractStringFromJsonString(taskJsonString, "question")

    val systemPrompt = "Answer to the given question using provided context only. Don't use any other sources of information. Your answer should be concise and strict."
    val userPrompt = taskMessage + separator + "Question: $taskQuestion" + separator + "Context: \n $taskInput"
    runBlocking {
        val answer = extractContent(openAI.chatCompletion(chatCompletionRequest(systemPrompt, userPrompt)))
        submitAnswer(authorizationToken, answer.toString())
        println("runBlocking - DONE")

    }
    println("DONE")
}

