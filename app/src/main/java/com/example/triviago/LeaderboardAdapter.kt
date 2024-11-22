package com.example.triviago

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.triviago.models.User

class LeaderboardAdapter(private val users: List<User>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    inner class LeaderboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userRank: TextView = view.findViewById(R.id.userRank)
        val userName: TextView = view.findViewById(R.id.userName)
        val userScore: TextView = view.findViewById(R.id.userScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_card, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val user = users[position]
        holder.userRank.text = (position + 1).toString()
        holder.userName.text = user.name
        holder.userScore.text = "Rating: ${user.score}"
    }
    override fun getItemCount() = users.size
}
