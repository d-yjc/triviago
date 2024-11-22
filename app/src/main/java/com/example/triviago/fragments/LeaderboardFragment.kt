package com.example.triviago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.triviago.LeaderboardAdapter
import com.example.triviago.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.triviago.models.User

class LeaderboardFragment : Fragment() {

    private lateinit var leaderboardRecyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        leaderboardRecyclerView = view.findViewById(R.id.leaderboardRecyclerView)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchLeaderboardData()

        return view
    }

    private fun fetchLeaderboardData() {
        db.collection("users")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = querySnapshot.documents.map { document ->
                    User(
                        name = document.getString("email") ?: "N/A",
                        score = document.getLong("score")?.toInt() ?: 0,
                        questionWins = document.getLong("questionWins")?.toInt() ?: 0,
                        questionLosses = document.getLong("questionLosses")?.toInt() ?: 0
                    )
                }
                leaderboardRecyclerView.adapter = LeaderboardAdapter(users)
            }
            .addOnFailureListener { e ->
                Log.e("LeaderboardFragment", "Error fetching leaderboard data", e)
            }

    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LeaderboardFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}