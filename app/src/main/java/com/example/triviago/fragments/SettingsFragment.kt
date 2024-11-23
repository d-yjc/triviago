package com.example.triviago.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.TimePickerDialog
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.triviago.R
import com.example.triviago.TriviaNotificationWorker
import com.example.triviago.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var switchEnableNotifications: SwitchMaterial
    private lateinit var layoutSetNotificationTime: LinearLayout
    private lateinit var textNotificationTimeValue: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        switchEnableNotifications = view.findViewById(R.id.switch_enable_notifications)
        layoutSetNotificationTime = view.findViewById(R.id.layout_set_notification_time)
        textNotificationTimeValue = view.findViewById(R.id.text_notification_time_value)

        sharedPreferences = requireContext()
            .getSharedPreferences("app_settings", Context.MODE_PRIVATE)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false)
        val notificationHour = sharedPreferences.getInt("notification_hour", 8)
        val notificationMinute = sharedPreferences.getInt("notification_minute", 0)

        switchEnableNotifications.isChecked = notificationsEnabled
        layoutSetNotificationTime.isEnabled = notificationsEnabled
        layoutSetNotificationTime.isClickable = notificationsEnabled
        textNotificationTimeValue.text = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            notificationHour,
            notificationMinute
        )

        switchEnableNotifications.setOnCheckedChangeListener { _, isChecked ->
            layoutSetNotificationTime.isEnabled = isChecked
            layoutSetNotificationTime.isClickable = isChecked
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()

            if (isChecked) {
                scheduleDailyNotification(notificationHour, notificationMinute)
                Toast.makeText(
                    requireContext(),
                    "Notifications Enabled", Toast.LENGTH_SHORT)
                    .show()
            } else {
                cancelDailyNotification()
                Toast.makeText(
                    requireContext(),
                    "Notifications Disabled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        layoutSetNotificationTime.setOnClickListener {
            val currentHour = sharedPreferences.getInt("notification_hour", 8)
            val currentMinute = sharedPreferences.getInt("notification_minute", 0)
            showTimePickerDialog(currentHour, currentMinute)
        }
    }

    private fun showDataDeletionConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete All Data")
            .setMessage(
                "Are you sure you want to delete all your data? This action cannot be undone."
            )
            .setPositiveButton("Delete") { dialog, _ ->
                deleteUserData()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(
            requireContext(),
            android.R.color.holo_red_dark)
        )
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
                }
        }
    }

    private fun showDeleteAccountConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage(
                "Are you sure you want to delete your account? This action cannot be undone."
            )
            .setPositiveButton("Confirm") { dialog, _ ->
                deleteUserAccount()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(
            requireContext(),
            android.R.color.holo_red_dark)
        )
    }

    private fun deleteUserAccount() {
        val user = mAuth.currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.delete()
                .addOnSuccessListener {
                    user.delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Your account has been deleted.", Toast.LENGTH_SHORT)
                                .show()
                            startLoginActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to delete account: ${e.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to delete data: ${e.message}", Toast.LENGTH_LONG)
                        .show()
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

    private fun showTimePickerDialog(currentHour: Int, currentMinute: Int) {
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                sharedPreferences.edit()
                    .putInt("notification_hour", hourOfDay)
                    .putInt("notification_minute", minute)
                    .apply()

                textNotificationTimeValue.text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    hourOfDay,
                    minute
                )
                scheduleDailyNotification(hourOfDay, minute)
                Toast.makeText(
                    requireContext(),
                    String.format(
                        Locale.getDefault(),
                        "Notification Time Set to %02d:%02d", hourOfDay, minute
                    ),
                    Toast.LENGTH_SHORT).show()
            },
            currentHour,
            currentMinute,
            true
        )
        timePicker.show()
    }

    private fun scheduleDailyNotification(hour: Int, minute: Int) {
        val currentTime = Calendar.getInstance()
        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (notificationTime.before(currentTime)) {
            notificationTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = notificationTime.timeInMillis - currentTime.timeInMillis
        val workRequest = PeriodicWorkRequestBuilder<TriviaNotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "DailyTriviaNotification",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelDailyNotification() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyTriviaNotification")
    }
}
