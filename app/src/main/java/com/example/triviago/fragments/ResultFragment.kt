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
    private var score: Int = 0
    private var totalQuestions: Int = 0

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
            score = it.getInt(ARG_SCORE)
            totalQuestions = it.getInt(ARG_TOTAL_QUESTIONS)
        }
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score/$totalQuestions"
        saveScore(score)
        val quitButton: Button = view.findViewById(R.id.quitButton)
        quitButton.setOnClickListener{
            //TODO: probably save the score to profile before finishing here.
            activity?.finish()
        }
        // Inflate the layout for this fragment
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

    companion object {

        private const val ARG_SCORE = "score"
        private const val ARG_TOTAL_QUESTIONS = "totalQuestions"
        @JvmStatic
        fun newInstance(score: Int, totalQuestions: Int) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SCORE, score)
                    putInt(ARG_TOTAL_QUESTIONS, totalQuestions)
                }
            }
    }
}