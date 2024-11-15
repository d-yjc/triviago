package com.example.triviago

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.triviago.activities.HomeActivity
import com.example.triviago.activities.LoginActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Check if user is already authenticated
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // User is already signed in
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        // Close MainActivity so it doesn't stay in the back stack
        finish()
    }
}