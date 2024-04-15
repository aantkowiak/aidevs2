package tasks

import kotlinx.coroutines.runBlocking
import utils.authorizeTask
import utils.extractStringFromJsonString
import utils.fetchTask
import utils.submitAnswer

fun main() {
    runBlocking {
        val authorizationToken = authorizeTask("helloapi")
        val answer = extractStringFromJsonString(fetchTask(authorizationToken), "cookie")
        submitAnswer(authorizationToken, answer)
    }
}