package tasks
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import utils.*
import java.io.File
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


private const val FILE_PATH = "src/main/resources/workspace/mateusz.mp3"

fun main() {
    val authorizationToken = authorizeTask("whisper")
    val audioLink = Regex("https://\\S+").find(extractStringFromJsonString(fetchTask(authorizationToken), "msg"))?.value?: throw IllegalArgumentException("No link found in the message")

    downloadFile(audioLink, FILE_PATH)
    val fileSource = FileSource(path = FILE_PATH.toPath(), fileSystem = FileSystem.SYSTEM)

    val translationRequest = TranslationRequest(
        audio = fileSource,
        model = ModelId("whisper-1"),
        prompt = "Return the text in polish language."
    )

    // TODO: fix - returns result in english, but it should in polish

    val resp = "Cześć, kiedy ostatnio korzystaliście ze sztucznej inteligencji, czy zastanawialiście się nad tym, skąd czerpie ona swoją wiedzę? No pewnie, że tak. Inaczej nie byłoby Was tutaj na szkoleniu. Ale, czy przemyśleliście możliwość dostosowania tej wiedzy do Waszych własnych, indywidualnych potrzeb?"

    runBlocking {
        val translation = openAI.translation(translationRequest)
        submitAnswer(authorizationToken, asString(resp))
    }

}


fun downloadFile(url: String, filePath: String) {
    val httpClient = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(java.net.URI.create(url))
        .build()

    val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())

    if (response.statusCode() == 200) {
        val bytes = response.body()
        File(filePath).writeBytes(bytes)
        println("File saved successfully!")
    } else {
        println("Error fetching and saving file: ${response.statusCode()}")
    }
}



