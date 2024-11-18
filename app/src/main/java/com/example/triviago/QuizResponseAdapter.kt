package com.example.triviago

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
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
        holder.categoryTextView.text = "Category: ${response.category ?: "Unknown"}"
        holder.scoreTextView.text = "Score: ${response.totalScore}"
        holder.dateTextView.text = "Date: ${formatDate(response.date)}"
        holder.typeTextView.text = "Type: ${response.isBoolean}"
    }

    override fun getItemCount(): Int = quizzes.size

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }



}