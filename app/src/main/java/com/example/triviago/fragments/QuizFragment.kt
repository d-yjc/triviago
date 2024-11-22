package com.example.triviago.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.triviago.activities.GameActivity
import com.example.triviago.models.Question
import com.example.triviago.R

class QuizFragment : Fragment() {

    private lateinit var question: Question
    private var questionNumber: Int = 0
    private var totalQuestions: Int = 0

    // Declaring views
    private lateinit var questionTextView: TextView
    private lateinit var questionNumberTextView: TextView
    private lateinit var optionButtons: List<Button>
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var booleanOptionsLayout: LinearLayout
    private lateinit var multipleChoiceOptionsLayout: LinearLayout

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)

        arguments?.let {
            question = it.getParcelable(ARG_QUESTION, Question::class.java)!!
            questionNumber = it.getInt(ARG_QUESTION_NUMBER, 0)
            totalQuestions = it.getInt(ARG_TOTAL_QUESTIONS, 0)
        }

        questionTextView = view.findViewById(R.id.questionTextView)
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView)
        booleanOptionsLayout = view.findViewById(R.id.booleanOptionsLayout)
        multipleChoiceOptionsLayout = view.findViewById(R.id.multipleChoiceOptionsLayout)
        trueButton = view.findViewById(R.id.trueButton)
        falseButton = view.findViewById(R.id.falseButton)
        optionButtons = listOf(
            view.findViewById<Button>(R.id.optionButton1),
            view.findViewById<Button>(R.id.optionButton2),
            view.findViewById<Button>(R.id.optionButton3),
            view.findViewById<Button>(R.id.optionButton4)
        )

        // Update views with question data
        questionTextView.text = Html.fromHtml(question.questionText, Html.FROM_HTML_MODE_LEGACY)
        questionNumberTextView.text = buildString {
            append("Question ")
            append(questionNumber)
            append(" of ")
            append(totalQuestions)
        }
        displayOptions()

        return view
    }

    private fun displayOptions() {
        if (question.isBooleanType) {
            //For boolean questions
            booleanOptionsLayout.visibility = View.VISIBLE
            multipleChoiceOptionsLayout.visibility = View.GONE

            trueButton.setOnClickListener { checkAnswer("True") }
            falseButton.setOnClickListener { checkAnswer("False") }
        } else {
            //For multiple-choice questions
            booleanOptionsLayout.visibility = View.GONE
            multipleChoiceOptionsLayout.visibility = View.VISIBLE

            question.options.forEachIndexed { index, option ->
                if (index < optionButtons.size) {
                    optionButtons[index].text = Html.fromHtml(option, Html.FROM_HTML_MODE_LEGACY)
                    optionButtons[index].visibility = View.VISIBLE
                    optionButtons[index].setOnClickListener {
                        checkAnswer(option)
                    }
                }
            }
        }
    }

    private fun checkAnswer(selectedAnswer: String) {
        (activity as? GameActivity)?.handleAnswerSelection(selectedAnswer, question.answer)
    }

    companion object {
        private const val ARG_QUESTION = "question"
        private const val ARG_QUESTION_NUMBER = "questionNumber"
        private const val ARG_TOTAL_QUESTIONS = "totalQuestions"
        fun newInstance(
            question: Question,
            questionNumber: Int,
            totalQuestions: Int) = QuizFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_QUESTION, question)
                putInt(ARG_QUESTION_NUMBER, questionNumber)
                putInt(ARG_TOTAL_QUESTIONS, totalQuestions)
            }
        }
    }
}
