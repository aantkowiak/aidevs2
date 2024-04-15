package utils

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.net.HttpURLConnection
import java.net.URL

suspend fun fetchPage(pageURL: String, noMoreThan: Int = 5, initialTimeout: Long = 5000): String {
    var tries = noMoreThan
    var timeout = initialTimeout
    var data = ""

    while (tries > 0) {
        delay(3000)

        try {
            val url = URL(pageURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")

            val response = runBlocking {
                withTimeout(timeout) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                }
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("HTTP error: ${connection.responseCode}")
            }

            data = response
            break
        } catch (error: Exception) {
            println("Attempt failed: ${error.message}")
            tries--
            timeout += 5000
        }
    }

    return data
}

fun fetchJsonStringFromUrl(url: String): String {
    val url = URL(url)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val inputStream = connection.inputStream
        return inputStream.bufferedReader().use { it.readText() }
    } else {
        throw Exception("Error fetching data: $responseCode")
    }
}

//
//val client = HttpClient()
//
//suspend fun fetchPage(pageURL: String, noMoreThan: Int = 5, initialTimeout: Long = 5000): String {
//    val retryPolicy = constantDelay(delayMillis = initialTimeout) + stopAtAttempts(noMoreThan)
//    var data = ""
//    retry(retryPolicy) {
//        val response: HttpResponse = client.get(pageURL) {
//            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
//        }
//        if (!response.status.isSuccess()) {
//            throw Exception("HTTP error: ${response.status}")
//        }
//        data = response.readText()
//    }
//    return data
//}