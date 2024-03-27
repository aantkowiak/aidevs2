package utils

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import kotlin.time.Duration.Companion.seconds
import com.aallam.openai.client.OpenAI


val openAI = OpenAI(
    token = System.getenv("OPENAPI_KEY"),
    timeout = Timeout(socket = 60.seconds),
)

fun chatCompletionRequest(systemPrompt: String, userPrompt: String): ChatCompletionRequest {
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = systemPrompt
            ),
            ChatMessage(
                role = ChatRole.User,
                content = userPrompt
            )
        )
    )
    return chatCompletionRequest
}

fun extractContent(nameCompletion: ChatCompletion) =
    nameCompletion.choices[nameCompletion.choices.lastIndex].message.content


val separator = "\n\n #### "