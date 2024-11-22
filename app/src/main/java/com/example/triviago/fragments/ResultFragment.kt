package com.example.triviago.fragments

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
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
    private var points: Int = 0
    private var numQuestions: Int = 0
    private var type: String = ""
    private var difficulty: String = ""

    private var mediaPlayer: MediaPlayer? = null

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
            points = it.getInt(ARG_POINTS, 0)
            numQuestions = it.getInt(ARG_TOTAL_QUESTIONS, 0)
            type = it.getString(ARG_TYPE).toString()
            difficulty = it.getString(ARG_DIFFICULTY).toString()
        }
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score/$numQuestions"

        val pointTextView: TextView = view.findViewById(R.id.pointTextView)
        pointTextView.text = "Points gained: $points"
        val lottieAnimation = view.findViewById<LottieAnimationView>(R.id.celebrationAnimation)

        scoreTextView.translationY = -50f
        scoreTextView.alpha = 0f
        pointTextView.translationY = -50f
        pointTextView.alpha = 0f

        animateScoreAndPoint(scoreTextView, pointTextView)

        lottieAnimation.playAnimation()


        playResultSound()

        //animateTextView(scoreTextView)
        //animateTextView(pointTextView)

        saveScore(points, score)
        saveQuizResponse()
        val quitButton: Button = view.findViewById(R.id.quitButton)
        quitButton.setOnClickListener{
            activity?.finish()
        }
        return view
    }

    private fun animateScoreAndPoint(scoreTextView: TextView, pointTextView: TextView) {
        // Score animation (fade in and slide down)
        val scoreFadeIn = ObjectAnimator.ofFloat(scoreTextView, "alpha", 0f, 1f)
        val scoreSlideDown = ObjectAnimator.ofFloat(scoreTextView, "translationY", -100f, 0f)
        val scoreAnimatorSet = AnimatorSet().apply {
            playTogether(scoreFadeIn, scoreSlideDown)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Point animation (fade in and slide down)
        val pointFadeIn = ObjectAnimator.ofFloat(pointTextView, "alpha", 0f, 1f)
        val pointSlideDown = ObjectAnimator.ofFloat(pointTextView, "translationY", -100f, 0f)
        val pointAnimatorSet = AnimatorSet().apply {
            playTogether(pointFadeIn, pointSlideDown)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Play animations in sequence
        AnimatorSet().apply {
            playSequentially(scoreAnimatorSet, pointAnimatorSet)
            start()
        }
    }

    private fun playResultSound() {
        // Initialize MediaPlayer with the result sound
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.result)
        mediaPlayer?.start()
    }

    private fun animateTextView(textView: TextView) {
        ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply {
            duration = 500 // 500ms fade-in duration
            start()
        }
    }

    private fun saveScore(points: Int, correctAnswers: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let { user ->
            val userDoc = db.collection("users").document(user.uid)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(userDoc)
                val currentScore = snapshot.getLong("score") ?: 0
                val updatedScore = currentScore + points

                val questionWins = snapshot.getLong("questionWins") ?: 0
                val questionLosses = snapshot.getLong("questionLosses") ?: 0
                val updatedWins = questionWins + correctAnswers
                val updatedLosses = questionLosses + (numQuestions - correctAnswers)

                transaction.update(userDoc, "questionWins", updatedWins)
                transaction.update(userDoc, "questionLosses", updatedLosses)
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
            "numQuestions" to arguments?.getInt("numQuestions"),
            "type" to arguments?.getString("type")
        )

        user?.let {
            db.collection("users").document(user.uid).collection("responses")
                .add(quizData)
                .addOnSuccessListener {
                    Log.d("QuizResponse", "com.example.triviago.Quiz response saved successfully")
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
        private const val ARG_TYPE = "type"
        private const val ARG_DIFFICULTY = "difficulty"
        private const val ARG_POINTS = "points"
        @JvmStatic
        fun newInstance(category: String, score: Int, points: Int, numQuestions: Int, type: String, difficulty: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                    putInt(ARG_SCORE, score)
                    putInt(ARG_POINTS, points)
                    putInt(ARG_TOTAL_QUESTIONS, numQuestions)
                    putString(ARG_TYPE, type)
                    putString(ARG_DIFFICULTY, difficulty)
                }
            }
    }
}