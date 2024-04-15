package tasks

import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import utils.*

fun main() {
    runBlocking {
        val authorizationToken = authorizeTask("functions")
        fetchTask(authorizationToken)

        val jsonObject = buildFunctionJsonObject("addUser", "add new user to the database", buildParameters(JsonObject().apply {
            addFunctionProp("name", "string", "provide name of the user")
            addFunctionProp("surname", "string", "provide surname of the user")
            addFunctionProp("year", "integer", "provide year of birth of the user")
        }))

        submitAnswer(authorizationToken, jsonObject.toString())
    }
}

