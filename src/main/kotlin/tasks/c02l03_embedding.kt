package tasks
import com.aallam.openai.api.embedding.EmbeddingResponse
import kotlinx.coroutines.runBlocking
import utils.*


fun main() {

    val authorizationToken = authorizeTask("embedding")

    fetchTask(authorizationToken)

    val embeddingRequestInputs = listOf("Hawaiian pizza")

    val embeddingRequest = buildEmbeddingRequest(embeddingRequestInputs)

    runBlocking {
        val embeddingResponse: EmbeddingResponse = openAI.embeddings(embeddingRequest)
        val embeddings = embeddingResponse.embeddings.stream().map { i -> i.embedding }.toList()
        val answer = embeddings.joinToString(",")

        submitAnswer(authorizationToken, answer)
    }
}
