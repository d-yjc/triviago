package com.example.triviago

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale


class QuizResponseAdapter(
    private val quizzes: List<QuizResponse>) :
    RecyclerView.Adapter<QuizResponseAdapter.QuizViewHolder>() {
        class QuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
            val scoreTextView: TextView = view.findViewById(R.id.quizScore)
            val dateTextView: TextView = view.findViewById(R.id.quizDate)
            val typeTextView: TextView = view.findViewById(R.id.quizType)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_response, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, index: Int) {
        val response = quizzes[index]
        holder.categoryTextView.text = "${response.category ?: "Unknown"}"
        holder.dateTextView.text = "${formatDate(response.date)}"
        holder.scoreTextView.text = "Score: ${response.score}/${response.numQuestions}"
        holder.typeTextView.text = if (response.isBooleanType) "True or False" else "Multiple Choice"
    }

    override fun getItemCount(): Int = quizzes.size

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val calender = Calendar.getInstance().apply { time = date }
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val daySuffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }

        val sdf = SimpleDateFormat("MMM", Locale.getDefault())
        var resultDate = sdf.format(date)
        resultDate = "$day$daySuffix $resultDate"
        return resultDate
    }
}
