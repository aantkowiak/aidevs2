package tasks
import utils.*


suspend fun main() {

    val authorizationToken = authorizeTask("inprompt")

    val taskJsonString = fetchTask(authorizationToken)
    val taskInput = extractArrayFromJsonString(taskJsonString, "input")
    val taskQuestion = extractStringFromJsonString(taskJsonString, "question")

    val nameSystemPrompt = "Answer with name and only one word - name"
    val nameUserPrompt = "Extract name of a person from the following test: $taskQuestion"
    val nameCompletion = openAI.chatCompletion(chatCompletionRequest(nameSystemPrompt, nameUserPrompt))
    val name = extractContent(nameCompletion)

    val filteredTaskInput = taskInput.filter { i -> i.lowercase().contains((name!!).lowercase()) }.toList()

    val answerSystemPrompt = "Answer to the given question using context information only. Don't use any other sources of information. Your answer should be concise and strict."
    val answerUserPrompt = separator + "Question: $taskQuestion" + separator + "Context: \n $filteredTaskInput"
    val answer =
        extractContent(openAI.chatCompletion(chatCompletionRequest(answerSystemPrompt, answerUserPrompt)))

    submitAnswer(authorizationToken, answer!!)
}


