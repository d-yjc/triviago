package com.example.triviago

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.triviago.R
import com.example.triviago.activities.GameActivity
import com.example.triviago.Quiz
import com.google.android.material.card.MaterialCardView

class RecommendedQuizAdapter(
    private val context: Context,
    private val quizList: List<Quiz>,
    private val itemClickListener: (Quiz) -> Unit
) : RecyclerView.Adapter<RecommendedQuizAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.quiz_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.quiz_description)
        val cardView: MaterialCardView = itemView.findViewById(R.id.recommended_quiz_card)

        fun bind(quiz: Quiz) {
            titleTextView.text = quiz.title
            descriptionTextView.text = quiz.description

            // Set click listener on the card view
            cardView.setOnClickListener {
                itemClickListener(quiz)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommended_quiz, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizList[position]
        holder.bind(quiz)
    }

    override fun getItemCount(): Int = quizList.size
}
