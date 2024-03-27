package utils

import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

fun authorizeTask(taskName: String): String {
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

    return extractStringFromJsonString(connection.inputStream.bufferedReader().readText(), "token")
}

fun fetchTask(token: String): String {
    val url = URL("https://tasks.aidevs.pl/task/$token")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val taskJson = connection.inputStream.bufferedReader().readText()
    consoleSeparator()
    println(taskJson)
    consoleSeparator()
    return taskJson
}

fun extractStringFromJsonString(jsonString: String, fieldName: String): String {
    val jsonObject = JSONObject(jsonString)
    return jsonObject.getString(fieldName)
}
fun extractArrayFromJsonString(jsonString: String, fieldName: String): List<String> {
    val jsonObject = JSONObject(jsonString)
    return jsonObject.getJSONArray(fieldName).toList()
        .filterIsInstance<String>().map { it }
        .filter(Objects::nonNull).toList()
}

fun submitAnswer(token: String, answer: String, isArray: Boolean = false) {
    val url = URL("https://tasks.aidevs.pl/answer/$token")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")

    val stringAnswer = "\"$answer\""
    val answerBody = if (isArray) answer else stringAnswer
    val postData = "{ \"answer\": $answerBody }"
    connection.doOutput = true
    val wr = OutputStreamWriter(connection.outputStream)
    wr.write(postData)
    wr.flush()
    consoleSeparator()
    println("SUBMISSION RESPONSE: ${connection.inputStream.bufferedReader().readText()}")
}

private fun consoleSeparator() {
    println("-".repeat(10))
}