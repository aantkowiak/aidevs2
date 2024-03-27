package tasks

import com.aallam.openai.api.chat.ChatCompletion
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import utils.authorizeTask
import utils.chatCompletionRequest
import utils.openAI
import utils.submitAnswer


fun main() {

    val authorizationToken = authorizeTask("liar")
    val url = "https://tasks.aidevs.pl/task/$authorizationToken"

    runBlocking {
        val systemPrompt =
            "Act as an support assistant to verify if the given answer is relevant to the questions. Answer strictly with 'YES' or 'NO'."
        val question = "What is the capital of Poland?"
        val answerToVerify = sendFormWithQuestion(url, question)
        val userPrompt = "Question: $question Answer: $answerToVerify"
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest(systemPrompt, userPrompt))
        val answer = completion.choices[completion.choices.lastIndex].message.content
        println(answer)
        submitAnswer(authorizationToken, answer!!)
    }
}

private suspend fun sendFormWithQuestion(url: String, question: String): String {
    val response: HttpResponse = HttpClient().submitForm(
        url = url,
        formParameters = parameters {
            append("question", question)
        }
    )
    return Gson().fromJson(response.bodyAsText(), Map::class.java)["answer"].toString()
}

