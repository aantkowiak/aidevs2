package tasks

import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.moderation.moderationRequest
import utils.*

suspend fun main() {
    val authorizationToken = authorizeTask("moderation")

    val taskPayload = extractArrayFromJsonString(fetchTask(authorizationToken), "input")

    val request: ModerationRequest = moderationRequest {
        input = taskPayload
    }
    val moderation = openAI.moderations(request)

    val answer = moderation.results.stream().map { i -> i.flagged }.map { i -> if (i) 1 else 0 }.toList()

    submitAnswer(authorizationToken, answer.toString(), true)
}
