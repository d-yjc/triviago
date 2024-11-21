package com.example.triviago.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

import com.example.triviago.OpenTdbAPIHandler
import com.example.triviago.Question
import com.example.triviago.R
import com.example.triviago.fragments.QuizFragment
import com.example.triviago.fragments.ResultFragment
import java.util.Locale

import kotlin.math.*

class GameActivity : AppCompatActivity() {

    private var category: String = ""
    private var type: String = ""
    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex: Int = 0
    private var score: Int = 0
    private var points: Int = 0
    private var difficulty: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        val apiUrl = intent.getStringExtra("apiUrl")
        val service = OpenTdbAPIHandler(this)

        if (apiUrl != null) {
            service.fetchAPI(apiUrl) { fetchedQuestions ->
                if (fetchedQuestions.isNotEmpty()) {
                    questions = fetchedQuestions
                    val firstQuestion = questions.first()
                    category = firstQuestion.category ?: "Unknown"
                    difficulty = firstQuestion.difficulty
                    type = if (firstQuestion.isBooleanType) "boolean" else "multiple"
                    loadFragment(
                        QuizFragment.newInstance(
                            questions[currentQuestionIndex],
                            currentQuestionIndex + 1,
                            questions.size
                        )
                    )
                } else {
                    Log.e("GameActivity", "Empty question list from apiUrl")
                }
            }
        } else {
            val numQuestions = intent.getIntExtra("numQuestions", 10)
            category = intent.getStringExtra("category") ?: "Any"
            difficulty = intent.getStringExtra("difficulty") ?: "Any"
            type = intent.getStringExtra("type") ?: "Any"

            service.fetchAPI(numQuestions, category, difficulty, type) { fetchedQuestions ->
                if (fetchedQuestions.isNotEmpty()) {
                    questions = fetchedQuestions
                    loadFragment(
                        QuizFragment.newInstance(
                            questions[currentQuestionIndex],
                            currentQuestionIndex + 1,
                            questions.size
                        )
                    )
                } else {
                    Log.e("GameActivity", "Empty question list")
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun handleAnswerSelection(selectedAnswer: String, correctAnswer: String) {
        val currQuestion = questions[currentQuestionIndex]
        if (selectedAnswer == correctAnswer) {
            score++
            points += calculateQuestionPoints(currQuestion)
        }
        loadNext()
    }

    private fun calculateQuestionPoints(question: Question): Int {
        val difficultyMultiplier = when (question.difficulty.lowercase(Locale.US)) {
            "easy" -> 1.0
            "medium" -> E
            "hard" -> E * E
            else -> 0.0
        }
        val typeMultiplier = if (question.isBooleanType) 0.5 else 1.0
        val calculatedScore = difficultyMultiplier * typeMultiplier

        return calculatedScore.roundToInt()
    }

    private fun loadNext() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            loadFragment(
                QuizFragment.newInstance(
                    questions[currentQuestionIndex],
                    currentQuestionIndex + 1,
                    questions.size
                )
            )
        } else {
            loadFragment(
                ResultFragment.newInstance(
                    category,
                    score,
                    points,
                    questions.size,
                    type,
                    difficulty
                )
            )
        }
    }

    private fun showExitConfirm() {
        AlertDialog.Builder(this)
            .setTitle("Exit Quiz")
            .setMessage("Are you sure you want to exit the quiz?")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showExitConfirm()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
