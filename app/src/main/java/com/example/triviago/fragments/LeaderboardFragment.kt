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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LeaderboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var leaderboardRecyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

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
        // Inflate the layout for this fragment

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
                        name = document.getString("email") ?: "N/A", //TODO: Adapt for usernames when implemented.
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LeaderboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LeaderboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}