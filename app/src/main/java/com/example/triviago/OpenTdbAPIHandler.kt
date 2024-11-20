package com.example.triviago

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONObject

class OpenTdbAPIHandler(private val context: Context) {

    fun fetchAPI(
        numQuestions: Int,
        category: String,
        difficulty: String,
        type: String,
        callback: (List<Question>) -> Unit
    ) {
        val url = buildURL(numQuestions, category, difficulty, type)
        Ion.with(context).load(url).asString().setCallback { e, result ->
            if (e != null) {
                Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show()
            } else {
                val questions = parseResponse(result)
                callback(questions)
            }
        }
    }

    private fun buildURL(numQuestions: Int, category: String, difficulty: String, type: String): String {
        val baseUrl = "https://opentdb.com/api.php?amount=$numQuestions"
        val categoryPart = if (category != "Any") "&category=${getCategoryId(category)}" else ""
        val difficultyPart =
            when (difficulty) {
                "Easy" -> "&difficulty=easy"
                "Medium" -> "&difficulty=medium"
                "Hard" -> "&difficulty=hard"
                else -> ""
            }
        val typePart = when (type) {
            "multiple" -> "&type=multiple"
            "boolean" -> "&type=boolean"
            else->""
        }
        val finalUrl = "$baseUrl$categoryPart$difficultyPart$typePart"
        Log.d("307Tag", "Generated URL: $finalUrl")
        return finalUrl
    }

    public fun parseResponse(response: String): MutableList<Question> {
        val jsonResponse = JSONObject(response)
        val results = jsonResponse.getJSONArray("results")
        val questions = mutableListOf<Question>()

        for (i in 0 until results.length()) {
            val question = results.getJSONObject(i)
            val questionText = question.getString("question")
            val correctAnswer = question.getString("correct_answer")
            val incorrectAnswers = question.getJSONArray("incorrect_answers")
            val type = question.getString("type").equals("boolean")
            val difficulty = question.getString("difficulty")
            var options = fetchOptions(correctAnswer, incorrectAnswers)
            questions.add(Question(questionText, type, correctAnswer, options, difficulty))
        }
        return questions
    }

    private fun fetchOptions(correctAnswer: String, incorrectAnswers: JSONArray): MutableList<String> {
        val options = mutableListOf<String>()
        for (i in 0 until incorrectAnswers.length()) {
            options.add(incorrectAnswers.getString(i))
        }
        options.add(correctAnswer)
        options.shuffle()

        return options
    }

    private fun getCategoryId(category: String): Int {
        val categoryTitles = context.resources.getStringArray(R.array.categories)
        val categoryIds = (9..32).toList().toIntArray()
        val index = categoryTitles.indexOf(category)

        if (index == -1) {
            Log.e("OpenTDBService", "Category not found: $category. Returning default ID 0.")
            return 0
        }
        return categoryIds[index]
    }
}