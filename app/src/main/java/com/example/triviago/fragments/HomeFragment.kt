package com.example.triviago.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triviago.models.Quiz
import com.example.triviago.R
import com.example.triviago.RecommendedQuizAdapter
import com.example.triviago.activities.GameActivity
import com.google.android.material.slider.Slider
import org.xmlpull.v1.XmlPullParser



class HomeFragment : Fragment() {
    private lateinit var recommendedQuizAdapter: RecommendedQuizAdapter
    private lateinit var recommendedQuizzesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setUpQuizOptions(view)

        val recommendedQuizzes = getRecommendedQuizzes()
        setUpRecommendedQuizzesRecyclerView(view, recommendedQuizzes)

        return view
    }

    private fun getRecommendedQuizzes(): List<Quiz> {
        val quizList = mutableListOf<Quiz>()
        val parser = resources.getXml(R.xml.recommended_quizzes)
        var eventType = parser.eventType
        var currentQuiz: Quiz? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (name == "quiz") {
                        currentQuiz = Quiz(title = "", description = "", apiUrl = "", icon = 0)
                    } else if (currentQuiz != null) {
                        when (name) {
                            "title" -> currentQuiz.title = parser.nextText()
                            "description" -> currentQuiz.description = parser.nextText()
                            "apiUrl" -> currentQuiz.apiUrl = parser.nextText()
                            "icon" -> {
                                val iconName = parser.nextText()
                                currentQuiz.icon = getDrawableResIdByName(requireContext(), iconName)
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (name == "quiz" && currentQuiz != null) {
                        quizList.add(currentQuiz)
                        currentQuiz = null
                    }
                }
            }
            eventType = parser.next()
        }

        return quizList
    }

    private fun getDrawableResIdByName(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "drawable", context.packageName)
    }


    private fun setUpRecommendedQuizzesRecyclerView(view: View, quizList: List<Quiz>) {
        recommendedQuizzesRecyclerView = view.findViewById(R.id.recommended_quizzes_recycler_view)
        recommendedQuizAdapter = RecommendedQuizAdapter(requireContext(), quizList) { quiz ->
            startGameActivityWithQuiz(quiz)
        }
        recommendedQuizzesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recommendedQuizzesRecyclerView.adapter = recommendedQuizAdapter
    }

    private fun startGameActivityWithQuiz(quiz: Quiz) {
        val intent = Intent(requireContext(), GameActivity::class.java).apply {
            putExtra("apiUrl", quiz.apiUrl)
        }
        startActivity(intent)
    }

    private fun setUpQuizOptions(view: View) {
        //Questions Slider
        val questionSlider: Slider = view.findViewById(R.id.num_questions_slider)
        questionSlider.valueFrom = 1f
        questionSlider.valueTo = 50f
        questionSlider.value = 10f //Default value to 10 questionizzies.
        questionSlider.setLabelFormatter { value -> value.toInt().toString() }
        questionSlider.trackActiveTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.primary)!!
        //Question-type Toggle Group
        val typeToggleGroup: com.google.android.material.button.MaterialButtonToggleGroup =
            view.findViewById(R.id.question_type_toggle_group)

        //Set default selection to multiple-choice
        typeToggleGroup.check(R.id.btn_multiple_choice)

        //Difficulty Spinner
        val difficultySpinner: Spinner = view.findViewById(R.id.difficulty_spinner)
        val difficulties = resources.getStringArray(R.array.difficulties)
        val difficultyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, difficulties
        )
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = difficultyAdapter

        //Category Spinner
        val categorySpinner: Spinner = view.findViewById(R.id.category_spinner)
        val categories = resources.getStringArray(R.array.categories)
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, categories
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}