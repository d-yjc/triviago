package com.example.triviago.activities

import android.os.Bundle
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

class GameActivity : AppCompatActivity() {

    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex: Int = 0
    private var score: Int = 0

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
        val category = intent.getStringExtra("category") ?: "Any"
        val difficulty = intent.getStringExtra("difficulty") ?: "Any"
        val type = intent.getStringExtra("type")

        // Initialize the OpenTDBService to fetch questions
        val service = OpenTdbAPIHandler(this)
        service.fetchAPI(numQuestions, category, difficulty, type ?: "any") { fetchedQuestions ->
            questions = fetchedQuestions
            loadFragment(QuizFragment.Companion.newInstance(questions[currentQuestionIndex], currentQuestionIndex + 1, questions.size))
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
        if (selectedAnswer == correctAnswer) score++  // Increment score if answer is correct
        loadNext()
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
            loadFragment(ResultFragment.Companion.newInstance(score, questions.size))
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
