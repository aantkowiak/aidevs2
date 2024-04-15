package tasks

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import utils.*
import java.io.File

fun main() {

    val authorizationToken = authorizeTask("people")
    val taskJsonString = fetchTask(authorizationToken)
    val taskDataUrl = extractStringFromJsonString(taskJsonString, "data")
    val taskQuestion = extractStringFromJsonString(taskJsonString, "question")

    val peopleData = fetchPeopleData(taskDataUrl)
    println("SIZE" + peopleData.size)

    runBlocking {
        val name = askChatWithRules("Extract name (first name and last name of the person from the given question)", "people_rules_1.txt", taskQuestion)
        val firstName = name.split(" ")[0]
        val lastName = name.split(" ")[1]
        println("$printlnSeparator FN: $firstName")
        println("$printlnSeparator LN: $lastName")
        val person = peopleData.first { p -> p.imie == firstName && p.nazwisko == lastName }
        println(printlnSeparator + person.toString())
        val answer = askChatWithRules(
            "Przeanalizuję dane o osobie i odpowiem na pytanie, korzystając uwzględniając reguły." +
                    separator +  "O osobie: \nImię i nazwisko: $firstName $lastName \nInne informacje: ${person.o_mnie} \nUlubiony kolor osoby: ${person.ulubiony_kolor}",
            "people_rules_2.txt",
            "Pytanie: $taskQuestion"
        )
        println(printlnSeparator + "ANSWER: $answer")
        submitAnswer(authorizationToken, "\"$answer\"")
    }
    println("DONE")
}

private suspend fun askChatWithRules(mainTask: String, rulesFileURI: String, userPrompt: String): String {
    val rules = File("src/main/resources/$rulesFileURI").readText()
    val systemPrompt = mainTask + separator + "Reguły: \n$rules" + separator
    println(printlnSeparator)
    println("--- PROMPT ---")
    println(systemPrompt)
    println(userPrompt)
    return extractContent(openAI.chatCompletion(chatCompletionRequest(systemPrompt, userPrompt))).toString()
}

data class Person(
    val imie: String,
    val nazwisko: String,
    val wiek: Int,
    val o_mnie: String,
    val ulubiona_postac_z_kapitana_bomby: String,
    val ulubiony_serial: String,
    val ulubiony_film: String,
    val ulubiony_kolor: String
)

fun fetchPeopleData(url: String): List<Person> {
    val jsonString = fetchJsonStringFromUrl(url)
    val gson = Gson()
    return gson.fromJson(jsonString, Array<Person>::class.java).toList()
}



