package com.example.triviago.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.triviago.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.*
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val logCatTag = "307Tag"
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener{_ -> loginClick()}
        registerButton.setOnClickListener{v -> registerClick(v)}
    }

    override fun onStart() {
        super.onStart()
        Log.i(logCatTag, "in state: onStart")
        if (currentUser != null) {
            Log.i(logCatTag, "Current user: $currentUser")
        }
    }
    override fun onStop() {
        super.onStop()
        Log.i(logCatTag, "in state: onStop")
    }
    private fun loginClick() {
        Log.i(logCatTag, "Login button clicked.")
        mAuth.signInWithEmailAndPassword(
            emailInput.text.toString(),
            passwordInput.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                closeKeyboard()
                startHomeActivity()
            } else {
                closeKeyboard()
                displayMsg((loginButton), getString(R.string.login_failed))
            }
        }
    }

    private fun registerClick(view: View) {
        Log.i(logCatTag, "Register button clicked.")

        if (mAuth.currentUser != null) {
            displayMsg(registerButton, getString(R.string.register_failed))
        } else {
            mAuth.createUserWithEmailAndPassword(
                emailInput.text.toString(),
                passwordInput.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i(logCatTag, "createUserWithEmailAndPassword: Success")
                    createUserInFireStore(mAuth.currentUser)
                    closeKeyboard()
                    startHomeActivity()
                } else {
                    Log.i(logCatTag, "createUserWithEmailAndPassword: Failure", task.exception)
                    displayMsg(registerButton, getString(R.string.already_registered))
                }
            }
        }
    }

    private fun createUserInFireStore(user: FirebaseUser?) {
        user?.let {
            val userDocRef = firestore.collection("users").document(user.uid)
            userDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    val userData = mapOf(
                        "email" to user.email,
                        "score" to 0,
                        "questionWins" to 0,
                        "questionLosses" to 0
                    )
                    userDocRef.set(userData)
                        .addOnSuccessListener {
                            Log.d(logCatTag, "User data added to Firestore")
                        }
                        .addOnFailureListener { e ->
                            Log.e(logCatTag, "Failed adding user data to Firestore", e)
                        }
                }
            }
        }
    }
    private fun displayMsg(view: View, msg: String) {
        val sb = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        sb.show()
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
