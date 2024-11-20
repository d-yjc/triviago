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
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val userName: TextView = findViewById(R.id.tvUserName)
        val scoreTextView: TextView = findViewById(R.id.tvScores)
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
                scoreTextView.text = "Total score: $totalScore"
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
                            isBooleanType = doc.getBoolean("isBooleanType") == true
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