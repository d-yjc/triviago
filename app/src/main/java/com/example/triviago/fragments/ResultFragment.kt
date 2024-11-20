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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.*

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var category: String = ""
    private var score: Int = 0
    private var numQuestions: Int = 0
    private var isBooleanType: Boolean = false
    private var difficulty: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //val score = it.getString(ARG_SCORE, 0)
            //val numQuestions = it.getString(ARG_TOTAL_QUESTIONS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_result, container, false)
        arguments?.let {
            category = it.getString(ARG_CATEGORY).toString()
            score = it.getInt(ARG_SCORE, 0)
            numQuestions = it.getInt(ARG_TOTAL_QUESTIONS, 0)
            isBooleanType = it.getBoolean(ARG_IS_BOOLEAN, false)
            difficulty = it.getString(ARG_DIFFICULTY).toString()
        }
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score/$numQuestions"

        saveScore(calculateQuizPoints(score, difficulty, isBooleanType))
        saveQuizResponse()
        val quitButton: Button = view.findViewById(R.id.quitButton)
        quitButton.setOnClickListener{
            activity?.finish()
        }
        return view
    }

    private fun saveScore(score: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let { user ->
            val userDoc = db.collection("users").document(user.uid)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(userDoc)
                val currentScore = snapshot.getLong("score") ?: 0
                val updatedScore = currentScore + score

                val questionWins = snapshot.getLong("questionWins") ?: 0
                val questionLosses = snapshot.getLong("questionLosses") ?: 0
                val totalQuestions = arguments?.getInt(ARG_TOTAL_QUESTIONS) ?: 0
                val wins = score
                val losses = totalQuestions - wins
                val updatedWins = questionWins + wins
                val updatedLosses = questionLosses + losses
                transaction.update(userDoc, "questionWins", updatedWins)
                transaction.update(userDoc, "questionLosses", updatedLosses)
                transaction.update(userDoc, "score", updatedScore)
            }
        }
    }

    private fun calculateQuizPoints(score: Int, difficulty: String, isBooleanType: Boolean): Int {
        val result: Int = when (difficulty) {
            "Easy" -> score
            "Medium" -> score * E
            "Hard" -> score * E * E
            else -> 0
        } as Int
        val finalScore = if (isBooleanType) result / 2 else result

        // Log the calculated score for debugging
        Log.d("QuizPointsCalculator", "Score: $score, Difficulty: $difficulty, isBooleanType: $isBooleanType, Calculated Points: $finalScore")

        return finalScore
    }

    private fun saveQuizResponse() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val quizData = mapOf(
            "category" to arguments?.getString("category"),
            "date" to System.currentTimeMillis(),
            "score" to arguments?.getInt("score"),
            "numQuestions" to arguments?.getInt("numQuestions"),
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
        private const val ARG_TOTAL_QUESTIONS = "numQuestions"
        private const val ARG_IS_BOOLEAN = "isBooleanType"
        private const val ARG_DIFFICULTY = "difficulty"
        @JvmStatic
        fun newInstance(category: String, score: Int, numQuestions: Int, isBooleanType: Boolean, difficulty: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                    putInt(ARG_SCORE, score)
                    putInt(ARG_TOTAL_QUESTIONS, numQuestions)
                    putBoolean(ARG_IS_BOOLEAN, isBooleanType)
                    putString(ARG_DIFFICULTY, difficulty)
                }
            }
    }
}