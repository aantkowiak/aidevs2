package tasks

import kotlinx.coroutines.runBlocking
import utils.asString
import utils.authorizeTask
import utils.fetchTask
import utils.submitAnswer

fun main() {
    runBlocking {
        val authorizationToken = authorizeTask("ownapi")
        fetchTask(authorizationToken)
        submitAnswer(authorizationToken, asString("https://hook.eu2.make.com/r9ekn2bxpjl644w1hywwwbpwmlix919d"))
//        submitAnswer(authorizationToken, asString("https://ba79-193-28-84-40.ngrok-free.app/"))
        println("DONE")
    }
}


