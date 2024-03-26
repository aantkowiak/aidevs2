package tasks

import com.aallam.openai.api.chat.ChatCompletion
import utils.*

suspend fun main() {
    val authorizationToken = authorizeTask("blogger")

    val taskPayload =
        extractArrayFromJsonString(fetchTask(authorizationToken), "blog").filterIsInstance<String>().map { it }

    val separator = "\n\n #### "
    val instruction =
        "Write a blog entry (in Polish) about making Margherita pizza. As input you will receive a list of 4 " +
                "chapters that must appear in the result. You must return an array as a response " +
                "consisting of 4 boxes representing these four written chapters. \n\n"
    val input = "Input: $taskPayload"
    val example = "Example: [\"content 1\",\"content 2\",\"content 3\",\"content 4\"]"
    val userPrompt = instruction + separator + input + separator + example

    val systemPrompt = "You are food blogger."
    val chatCompletionRequest = chatCompletionRequest(systemPrompt, userPrompt)

    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

    val answer = completion.choices[completion.choices.lastIndex].message.content

    println("###### ANSWER  $answer")
    submitAnswer(authorizationToken, answer.toString(), true)
}


