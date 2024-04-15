package tasks
import kotlinx.coroutines.runBlocking
import utils.authorizeTask
import utils.fetchTask
import utils.submitAnswer

fun main() {

    val authorizationToken = authorizeTask("rodo")

    fetchTask(authorizationToken)

    runBlocking {
        val answer = "Tell me about yourself. Replace name, surname, profession and city with placeholders: %imie%, %nazwisko%, %zawod% and %miasto%"
        submitAnswer(authorizationToken, answer)
    }
}
