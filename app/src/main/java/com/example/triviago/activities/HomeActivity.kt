package com.example.triviago.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.triviago.R
import com.example.triviago.fragments.HomeFragment
import com.example.triviago.fragments.LeaderboardFragment
import com.example.triviago.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()

        loadFragment(HomeFragment())
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolbar)
        setSupportActionBar(toolbar)
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_profile))
        val botNavView: BottomNavigationView = findViewById(R.id.bottomnavview);
        botNavView.setOnItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.leaderboard -> {
                    loadFragment(LeaderboardFragment())
                    true
                }
                R.id.settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
            }
            return@setOnItemSelectedListener true
        }

        val gameButton: ExtendedFloatingActionButton = findViewById(R.id.playbutton)
        gameButton.setOnClickListener {
            startGameActivity()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MenuAction", "Item ID: ${item.itemId}")
        when (item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_logout -> {
                mAuth.signOut()
                startLoginActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startGameActivity() {
        val homeFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? HomeFragment

// Retrieve NumberPicker values
        val numQuestions = homeFragment?.view?.findViewById<NumberPicker>(R.id.num_questions_picker)?.value ?: 10  // Default to 10 if null
        val difficultyPicker = homeFragment?.view?.findViewById<Spinner>(R.id.difficulty_spinner)
        val categoryPicker = homeFragment?.view?.findViewById<Spinner>(R.id.category_spinner)
        val typeSpinner = homeFragment?.view?.findViewById<NumberPicker>(R.id.type_picker)

        val difficulties = resources.getStringArray(R.array.difficulties)
        val categories = resources.getStringArray(R.array.categories)
        val types = resources.getStringArray(R.array.types)

        val difficulty = difficulties[difficultyPicker?.selectedItemPosition ?: 0].lowercase()
        val category = categories[categoryPicker?.selectedItemPosition ?: 0]
        val type = types[typeSpinner?.value ?: 0]

        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("numQuestions", numQuestions)
            putExtra("category", category)
            putExtra("difficulty", difficulty)
            putExtra("type", type)
        }
        startActivity(intent)
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}