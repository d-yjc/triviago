package com.example.triviago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.triviago.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var category: String = ""
    private var score: Int = 0
    private var totalQuestions: Int = 0
    private var isBooleanType: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val score = it.getString(ARG_SCORE)
            val totalQuestions = it.getString(ARG_TOTAL_QUESTIONS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_result, container, false)
        arguments?.let {
            category = it.getString(ARG_CATEGORY).toString()
            score = it.getInt(ARG_SCORE)
            totalQuestions = it.getInt(ARG_TOTAL_QUESTIONS)
            isBooleanType = it.getBoolean(ARG_IS_BOOLEAN)
        }
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score/$totalQuestions"
        saveScore(score)
        saveQuizResponse()
        val quitButton: Button = view.findViewById(R.id.quitButton)
        quitButton.setOnClickListener{
            activity?.finish()
        }
        return view
    }

    private fun saveScore(points: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val scoreData = mapOf(
            "score" to score,
            "timestamp" to System.currentTimeMillis()
        )

        user?.let { user ->
            val userDoc = db.collection("users").document(user.uid)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(userDoc)
                val currentScore = snapshot.getLong("score") ?: 0
                val updatedScore = currentScore + points
                transaction.update(userDoc, "score", updatedScore)
            }
        }
    }

    private fun saveQuizResponse() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val quizData = mapOf(
            "category" to arguments?.getString("category"),
            "date" to System.currentTimeMillis(),
            "score" to arguments?.getInt("score"),
            "numQuestions" to arguments?.getInt("totalQuestions"),
            "isBooleanType" to arguments?.getBoolean("isBooleanType")
        )

        user?.let {
            db.collection("users").document(user.uid).collection("responses")
                .add(quizData)
                .addOnSuccessListener {
                    Log.d("QuizResponse", "Quiz response saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("QuizResponse", "Error saving quiz response", e)
                }
        }
    }

    companion object {

        private const val ARG_CATEGORY = "category"
        private const val ARG_SCORE = "score"
        private const val ARG_TOTAL_QUESTIONS = "totalQuestions"
        private const val ARG_IS_BOOLEAN = "isBooleanType"
        @JvmStatic
        fun newInstance(category: String, score: Int, totalQuestions: Int, isBooleanType: Boolean) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                    putInt(ARG_SCORE, score)
                    putInt(ARG_TOTAL_QUESTIONS, totalQuestions)
                    putBoolean(ARG_IS_BOOLEAN, isBooleanType)
                }
            }
    }
}