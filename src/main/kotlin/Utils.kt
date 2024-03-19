package org.example

import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

fun authorize(taskName: String): String {
    val url = URL("https://tasks.aidevs.pl/token/$taskName")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")

    val API_KEY = "b703595f-aae5-4e20-8773-bd1a69139785"
    val postData = "{\"apikey\": \"$API_KEY\"}"
    connection.doOutput = true
    val wr = OutputStreamWriter(connection.outputStream)
    wr.write(postData)
    wr.flush()

    return extractTokenFromJson(connection.inputStream.bufferedReader().readText(), "token")
}

fun fetchTask(token: String): String {
    val url = URL("https://tasks.aidevs.pl/task/$token")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val taskJson = connection.inputStream.bufferedReader().readText()
    consoleSeparator()
    println(taskJson)
    return taskJson
}

fun extractTokenFromJson(jsonString: String, fieldName: String): String {
    val jsonObject = JSONObject(jsonString)
    return jsonObject.getString(fieldName)
}

fun submitAnswer(token: String, answer: String) {
    val url = URL("https://tasks.aidevs.pl/answer/$token")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")

    val postData = "{ \"answer\": \"$answer\" }"
    connection.doOutput = true
    val wr = OutputStreamWriter(connection.outputStream)
    wr.write(postData)
    wr.flush()
    consoleSeparator()
    println(connection.inputStream.bufferedReader().readText())
}

private fun consoleSeparator() {
    println("-".repeat(10))
}