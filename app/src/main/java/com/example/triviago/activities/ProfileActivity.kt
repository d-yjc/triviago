package com.example.triviago.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.triviago.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val userName: TextView = findViewById(R.id.tvUserName)
        val scoreTextView: TextView = findViewById(R.id.tvScores)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let { user ->
            userName.text = user.email

            db.collection("users").document(user.uid).get().addOnSuccessListener { document ->

                val totalScore = document.getLong("score") ?: 0
                scoreTextView.text = "Total score: $totalScore"
            }
        }

        val backButton: MaterialButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}