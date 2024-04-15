package tasks

import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import utils.*

fun main() {

    val authorizationToken = authorizeTask("knowledge")
    val taskJsonString = fetchTask(authorizationToken)

//    val taskQuestion = "jaki jest teraz kurs dolara?"
    val taskQuestion = extractStringFromJsonString(taskJsonString, "question")

    runBlocking {
        val categoryFuture = async {
            askChatWithRules(
                "Categorize the given question into one of three categories: 1 - currency, 2 - countries, 3 - other",
                "knowledge_rules_1.txt",
                "$separator QUESTION: $taskQuestion"
            ).toInt()
        }
        val category = categoryFuture.await()
        println(printlnSeparator)
        println("CATEGORY: $category")

        var answer: String? = null
        if (category == 1) {
            answer = answerCurrencyCategoryQuestion(taskQuestion)
        }
        if (category == 3) {
             answer = answerGeneralKnowledgeQuestion(taskQuestion)
        }
        if(answer != null) {
            println(printlnSeparator + "ANSWER: $answer")
            submitAnswer(authorizationToken, "\"$answer\"")
        }
    }
    println("DONE")
}

private suspend fun answerGeneralKnowledgeQuestion(taskQuestion: String) = askChatWithRules(
    "Answer the given question taking into consideration the rules.",
    "knowledge_rules_2.txt",
    "$separator QUESTION: $taskQuestion"
)

private suspend fun answerCurrencyCategoryQuestion(taskQuestion: String): String {
    val currencyCode = askChatWithRules(
        "Extract the currency code (eg. USD, EUR, CHF, GBP) that is the subject of the given question. You need to strictly follow the rules.",
        "knowledge_rules_2.txt",
        "$separator QUESTION: $taskQuestion"
    )
    val currencyData = fetchCurrencyData("https://api.nbp.pl/api/exchangerates/tables/A?format=json")
    return currencyData.rates.first { i -> i.code == currencyCode }.mid.toString()
}

fun fetchCurrencyData(url: String): CurrencyResponse {
    val jsonString = fetchJsonStringFromUrl(url)
    val gson = Gson()
    return gson.fromJson(jsonString, Array<CurrencyResponse>::class.java).first()
}

data class CurrencyResponse(
    val table: String,
    val no: String,
    val effectiveDate: String,
    val rates: List<CurrencyRate>
)

data class CurrencyRate(
    val currency: String,
    val code: String,
    val mid: Double
)
