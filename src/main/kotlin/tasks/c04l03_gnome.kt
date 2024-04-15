package tasks

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.*

fun main() {
    runBlocking {
        val authorizationToken = authorizeTask("gnome")
        val taskJsonString = fetchTask(authorizationToken)
        val taskQuestion =
            "Return the color of the Hat of the Gnome using POLISH language. If there is no hat or not gnome, return ERROR as answer. Return only one word"
        val taskUrl = extractStringFromJsonString(taskJsonString, "url")

        val answer = async {
            askChatVision(taskQuestion, taskUrl);
        }
        submitAnswer(authorizationToken, asString(answer.await()))
    }
    println("DONE")
}

