package com.example.triviago.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.example.triviago.activities.LoginActivity
import com.example.triviago.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mAuth: FirebaseAuth
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

        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        mAuth = FirebaseAuth.getInstance()

        val layoutDataManagement = view.findViewById<ImageButton>(R.id.button_data_management)
        layoutDataManagement.setOnClickListener {
            showDataDeletionConfirmationDialog()
        }

        val layoutLogout = view.findViewById<ImageButton>(R.id.button_logout)
        layoutLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val layoutDeleteAccount = view.findViewById<ImageButton>(R.id.button_delete_account)
        layoutDeleteAccount.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }
        return view
    }

    private fun showDataDeletionConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All Data")
            .setMessage("Are you sure you want to delete all your data? This action cannot be undone.")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteUserData()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteUserData() {
        val user = mAuth.currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            // First, delete all documents in 'responses' subcollection
            val responsesCollectionRef = userDocRef.collection("responses")
            responsesCollectionRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = db.batch()
                    for (document in querySnapshot.documents) {
                        batch.delete(document.reference)
                    }
                    // Commit the batch
                    batch.commit()
                        .addOnSuccessListener {
                            userDocRef.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "All your data has been deleted.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Failed to delete user data: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to delete responses: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to retrieve responses: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun showDeleteAccountConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Confirm") { dialog, _ ->
                deleteUserAccount()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteUserAccount() {
        val user = mAuth.currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.delete()
                .addOnSuccessListener {
                    // Then, delete the user's account
                    user.delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Your account has been deleted.", Toast.LENGTH_SHORT).show()
                            startLoginActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to delete account: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to delete data: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { dialog, _ ->
                mAuth.signOut()
                startLoginActivity()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
