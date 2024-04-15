package tasks

import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import utils.*
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    runBlocking {
        val authorizationToken = authorizeTask("tools")
        val taskJsonString = fetchTask(authorizationToken)
        val taskQuestion = extractStringFromJsonString(taskJsonString, "question")
        val systemPrompt = File("src/main/resources/tools_prompt.txt").readText() + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        val answer = async { askChatWithRules(mainTask = systemPrompt, userPrompt = taskQuestion) }.await()
        submitAnswer(authorizationToken, answer)
        println("DONE")
    }
}



