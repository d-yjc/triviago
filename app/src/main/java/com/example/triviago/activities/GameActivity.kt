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

        // Setup toolbar as action bar with back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Show back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)  // Customize icon

        // Fetch quiz data
        val numQuestions = intent.getIntExtra("numQuestions", 10)
        category = intent.getStringExtra("category") ?: "Any"
        difficulty = intent.getStringExtra("difficulty") ?: "Any"
        type = intent.getStringExtra("type") ?: "Any"

        // Initialize the OpenTDBService to fetch questions
        val service = OpenTdbAPIHandler(this)
        service.fetchAPI(numQuestions, category, difficulty, type) { fetchedQuestions ->
            if (fetchedQuestions.isNotEmpty()) {
                questions = fetchedQuestions
                loadFragment(QuizFragment.Companion.newInstance(
                    questions[currentQuestionIndex],
                    currentQuestionIndex + 1,
                    questions.size))
            } else {
                Log.e("307TomTag", "Empty question list")
            }
        }
    }

    // Method to load the given fragment into the fragment container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Handles the user's answer selection and loads the next question or result screen
    fun handleAnswerSelection(selectedAnswer: String, correctAnswer: String) {
        val currQuestion = questions[currentQuestionIndex]
        if (selectedAnswer == correctAnswer) {
            score++
            points += calculateQuestionPoints(currQuestion)
        }
        loadNext()
    }

    private fun calculateQuestionPoints(question: Question): Int {
        val difficultyMultiplier = when (question.difficulty.lowercase()) {
            "easy" -> 1.0
            "medium" -> E
            "hard" -> E * E
            else -> 0.0
        }
        val typeMultiplier = if (question.isBooleanType) 0.5 else 1.0
        val calculatedScore = difficultyMultiplier * typeMultiplier

        return calculatedScore.roundToInt()
    }

    // Load the next question or show the result screen if all questions are answered
    private fun loadNext() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            loadFragment(
                QuizFragment.Companion.newInstance(
                    questions[currentQuestionIndex],
                    currentQuestionIndex + 1,
                    questions.size
                )
            )
        } else {
            Log.d("307TomTag", "Type: $type")
            loadFragment(ResultFragment.Companion.newInstance(
                category,
                score,
                points,
                questions.size,
                type,
                difficulty))
        }
    }

    // Show confirmation dialog when back button is pressed
    private fun showExitConfirm() {
        AlertDialog.Builder(this)
            .setTitle("Exit Quiz")
            .setMessage("Are you sure you want to exit the quiz?")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    // Handle toolbar back button press
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
