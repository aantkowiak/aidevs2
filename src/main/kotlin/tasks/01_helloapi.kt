package org.example.tasks

import org.example.authorize
import org.example.extractTokenFromJson
import org.example.fetchTask
import org.example.submitAnswer

fun main() {
    val authorizationToken = authorize("helloapi")
    val answer = extractTokenFromJson(fetchTask(authorizationToken), "cookie")
    submitAnswer(authorizationToken, answer)
}
