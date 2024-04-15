package utils

import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*


val printlnSeparator = "#".repeat(100) + "  "

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

fun submitAnswer(token: String, answerPayload: String) {
    val url = URI.create("https://tasks.aidevs.pl/answer/$token")
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(url)
        .POST(HttpRequest.BodyPublishers.ofString("{\"answer\": $answerPayload}"))
        .header("Content-Type", "application/json")
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    consoleSeparator()
    println("---------- SUBMITTED ANSWER ----------")
    println("POST BODY: {\"answer\": $answerPayload}")
    println(response.body())
}


fun asString(text: String): String {
    return "\"$text\""
}


//suspend fun submitAnswer(token: String, answerPayload: Any, answerType: AnswerType = AnswerType.STRING) {
//        val url = URL("https://tasks.aidevs.pl/answer/$token")
//        val connection = url.openConnection() as HttpURLConnection
//        connection.requestMethod = "POST"
//        connection.setRequestProperty("Content-Type", "application/json")
//
//        val stringAnswer = "\"$answerPayload\""
//        val answerBody = if (AnswerType.OBJECT == answerType) answerPayload else stringAnswer
//        val postData = "{ \"answer\": $answerBody }"
//        connection.doOutput = true
//        val wr = OutputStreamWriter(connection.outputStream)
//        wr.write(postData)
//        wr.flush()
//        consoleSeparator()
//        println("---------- SUBMISSION ANSWER ----------")
//        println("POST BODY: $postData")
//        println(" ${connection.inputStream.bufferedReader().readText()}")
//}

//suspend fun submitAnswer(token: String, answerPayload: Any, answerType: AnswerType = AnswerType.STRING) {
//    val client = HttpClient.newBuilder().build()
//    val jsonPayload = Json.encodeToString(answerPayload)
//    val inputStream = ByteArrayInputStream(jsonPayload.toByteArray())
//    val ofString = HttpRequest.BodyPublishers.ofString(answerPayload.toString())
//    val request = HttpRequest.newBuilder()
//        .POST(ofString)
//        .uri(URI.create("https://tasks.aidevs.pl/answer/$token"))
//        .build()
//
//    val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//    val body = response.await().body()
//    println("POST BODY: $body")
//}

private fun consoleSeparator() {
    println("-".repeat(10))
}