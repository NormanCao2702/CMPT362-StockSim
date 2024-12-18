package com.example.cmpt362_stocksim.ui.auth

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.MainActivity
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // Setup backend repository and data manager
    private lateinit var backendViewModel: BackendViewModel
    private lateinit var userDataManager: UserDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userDataManager = UserDataManager(this)

        // Hide action bar
        supportActionBar?.hide()

        // Check if user is already logged in
        if (isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Initialize ViewModel
        val repository = BackendRepository()
        val viewModelFactory = BackendViewModelFactory(repository)
        backendViewModel = viewModelFactory.create(BackendViewModel::class.java)

        // Find views
        val emailInput = findViewById<TextInputEditText>(R.id.etEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.etPassword)
        val loginButton = findViewById<MaterialButton>(R.id.btnLogin)
        val signUpText = findViewById<TextView>(R.id.tvSignUp)

        // Navigate to Register screen
        signUpText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // Basic validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading indicator (you might want to add a ProgressBar in your layout)
            loginButton.isEnabled = false

            // Attempt login
            lifecycleScope.launch {
                try {
                    val jwt = backendViewModel.login(email, password)

                    // Save JWT token
//                    saveJwtToken(jwt)
                    userDataManager.saveUserData(jwt)

                    // Navigate to MainActivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } catch (e: IllegalArgumentException) {
                    // Show error to user
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                        loginButton.isEnabled = true
                    }
                }
            }
        }
    }

    // Save users backend token
    private fun saveJwtToken(token: String) {
        // Save JWT token to SharedPreferences
        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("JWT_TOKEN", token)
            apply()
        }
    }
    // Check if user is logged in
    private fun isLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        val token = sharedPref.getString("JWT_TOKEN", null)
        return !token.isNullOrEmpty()
    }

}