package tasks

import com.aallam.openai.api.chat.ChatCompletion
import utils.*
import java.io.File

suspend fun main() {
    val authorizationToken = authorizeTask("blogger")

    val taskPayload =
        extractArrayFromJsonString(fetchTask(authorizationToken), "blog").map { it }

    val instruction = File("src/main/resources/pizza_prompt.txt").readText()

    val input = "Input: $taskPayload"
    val example = "Example: [\"content 1\",\"content 2\",\"content 3\",\"content 4\"]"
    val userPrompt = instruction + separator + input + separator + example

    val systemPrompt = "You are food blogger."
    val chatCompletionRequest = chatCompletionRequest(systemPrompt, userPrompt)

    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
    val answer = completion.choices[completion.choices.lastIndex].message.content

    submitAnswer(authorizationToken, answer.toString())
}


