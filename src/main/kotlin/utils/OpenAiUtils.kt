package utils

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import kotlin.time.Duration.Companion.seconds
import com.aallam.openai.client.OpenAI
import com.google.gson.JsonObject
import java.io.File


val openAI = OpenAI(
    token = System.getenv("OPENAPI_KEY"),
    timeout = Timeout(socket = 60.seconds),
)

fun chatCompletionRequest(systemPrompt: String, userPrompt: String): ChatCompletionRequest {
    val chatCompletionRequest = ChatCompletionRequest(
//        model = ModelId("gpt-3.5-turbo"),
        model = ModelId("gpt-4"),
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

fun chatVisionCompletionRequest(userPrompt: String, imageUrl: String): ChatCompletionRequest {
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-4-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = """
                    { type: "text", text: "$userPrompt" },
                    {
                      type: "image_url",
                      image_url: {
                        "url": "$imageUrl,
                      },
                    }
                """.trimIndent()
            )
        )
    )
    return chatCompletionRequest
}

fun extractContent(nameCompletion: ChatCompletion) =
    nameCompletion.choices[nameCompletion.choices.lastIndex].message.content


val separator = "\n####\n\n"


fun buildFunctionJsonObject(name: String, description: String, parametersJsonObject: JsonObject): JsonObject {
    val jsonObject = JsonObject().apply {
        addProperty("name", name)
        addProperty("description", description)
        add("parameters", parametersJsonObject)
    }
    return jsonObject
}

fun buildParameters(propertiesObject: JsonObject): JsonObject {
    val parametersObject = JsonObject().apply {
        addProperty("type", "object")
        add("properties", propertiesObject)
    }
    return parametersObject
}

fun JsonObject.addFunctionProp(name: String, type: String, description: String) {
    val nameObject = JsonObject().apply {
        addProperty("type", type)
        addProperty("description", description)
    }
    add(name, nameObject)
}

fun buildEmbeddingRequest(embeddingRequestInputs: List<String>) = EmbeddingRequest(
    model = ModelId("text-embedding-ada-002"),
    input = embeddingRequestInputs
)

suspend fun askChatWithRules(mainTask: String, rulesFileURI: String? = null, userPrompt: String): String {
    val rules = if(rulesFileURI != null) File("src/main/resources/$rulesFileURI").readText() else ""
    val systemPrompt = mainTask + separator + "Rules: \n$rules"
    println(printlnSeparator)
    println("--- PROMPT ---")
    println(systemPrompt)
    println(userPrompt)
    println(printlnSeparator)
    return extractContent(openAI.chatCompletion(chatCompletionRequest(systemPrompt, userPrompt))).toString()
}

suspend fun askChatVision(userPrompt: String, imageUrl: String): String {
    return extractContent(openAI.chatCompletion(chatVisionCompletionRequest(userPrompt, imageUrl))).toString()
}