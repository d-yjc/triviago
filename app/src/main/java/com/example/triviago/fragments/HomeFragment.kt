package com.example.triviago.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.core.content.ContextCompat
import com.example.triviago.R
import com.google.android.material.slider.Slider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Number of Questions Slider
        val questionSlider: Slider = view.findViewById(R.id.num_questions_slider)
        questionSlider.valueFrom = 1f
        questionSlider.valueTo = 50f
        questionSlider.value = 10f // Set default value
        questionSlider.setLabelFormatter { value -> value.toInt().toString() }
        questionSlider.trackActiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)!!
        // Question Type Toggle Group
        val typeToggleGroup: com.google.android.material.button.MaterialButtonToggleGroup =
            view.findViewById(R.id.question_type_toggle_group)
        val multipleChoiceButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_multiple_choice)
        val trueFalseButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_true_false)

        // Set default selection (optional)
        typeToggleGroup.check(R.id.btn_multiple_choice)

        // Difficulty Spinner
        val difficultySpinner: Spinner = view.findViewById(R.id.difficulty_spinner)
        val difficulties = resources.getStringArray(R.array.difficulties)
        val difficultyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficulties)
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = difficultyAdapter

        // Category Spinner
        val categorySpinner: Spinner = view.findViewById(R.id.category_spinner)
        val categories = resources.getStringArray(R.array.categories)
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}