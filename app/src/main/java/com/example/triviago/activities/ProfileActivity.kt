package com.example.triviago.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.triviago.*
import com.google.android.material.appbar.MaterialToolbar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val userName: TextView = findViewById(R.id.tvUserName)
        val scoreTextView: TextView = findViewById(R.id.tvScores)
        val tvWinLoss: TextView = findViewById(R.id.tvWinLoss)
        val tvWinRate: TextView = findViewById(R.id.tvWinRate)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        user?.let { user ->
            userName.text = user.email
            db.collection("users").document(user.uid).get().addOnSuccessListener { document ->
                val totalScore = document.getLong("score") ?: 0
                scoreTextView.text = buildString {
                    append("Rating: ")
                    append(totalScore)
                }
                val wins = document.getLong("questionWins") ?: 0
                val losses = document.getLong("questionLosses") ?: 0
                var winRate = (wins.toDouble() / (wins + losses).toDouble()) * 100
                winRate = String.format(Locale.US, "%.2f", winRate).toDouble()
                tvWinLoss.text = buildString {
                    append(wins)
                    append("W ")
                    append(losses)
                    append("L")
                }

                if (wins + losses > 0) {
                    var winRate = (wins.toDouble() / (wins + losses).toDouble()) * 100
                    winRate = String.format(Locale.US, "%.2f", winRate).toDouble()
                    tvWinRate.text = buildString {
                        append("Win rate ")
                        append(winRate)
                        append("%")
                    }
                } else {
                    tvWinRate.text = getString(R.string.null_winrate)
                }
            }
            fetchQuizHistory()
        }
    }

    private fun fetchQuizHistory() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let {
            db.collection("users").document(it.uid).collection("responses")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val quizHistory = result.map { doc ->
                        QuizResponse(
                            category = doc.getString("category") ?: "",
                            date = doc.getLong("date") ?: 0L,
                            score = (doc.getLong("score") ?: 0).toInt(),
                            numQuestions = (doc.getLong("numQuestions") ?: 0).toInt(),
                            type = doc.getString("type") ?: ""
                        )
                    }
                    displayQuizHistory(quizHistory)
                }
                .addOnFailureListener { e ->
                    Log.e("QuizHistory", "Error fetching quiz history", e)
                }
        }
    }

    private fun displayQuizHistory(pastQuizzes: List<QuizResponse>) {
        val recyclerView: RecyclerView = findViewById(R.id.pastQuizzesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuizResponseAdapter(pastQuizzes)
    }
}