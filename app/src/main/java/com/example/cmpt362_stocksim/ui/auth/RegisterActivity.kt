package com.example.cmpt362_stocksim.ui.auth

import android.app.DatePickerDialog
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
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var backendViewModel: BackendViewModel
    private lateinit var userDataManager: UserDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        userDataManager = UserDataManager(this)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize ViewModel
        val repository = BackendRepository()
        val viewModelFactory = BackendViewModelFactory(repository)
        backendViewModel = viewModelFactory.create(BackendViewModel::class.java)

        // Find views
        val usernameInput = findViewById<TextInputEditText>(R.id.etUsername)
        val emailInput = findViewById<TextInputEditText>(R.id.etEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.etPassword)
        val birthdayInput = findViewById<TextInputEditText>(R.id.etBirthday)
        val registerButton = findViewById<MaterialButton>(R.id.btnRegister)
        val signInText = findViewById<TextView>(R.id.tvSignIn)

        birthdayInput.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Open a DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1, // Month is 0-based, so add 1
                        selectedDay
                    )
                    birthdayInput.setText(selectedDate)
                },
                year, month, day
            )

            // Show the DatePickerDialog
            datePickerDialog.show()
        }

        // Navigate to Login screen
        signInText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val birthday = birthdayInput.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || birthday.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val jwt = backendViewModel.register(username, email, password, birthday)

                    // Save JWT token (you'll need to implement this)
//                    saveJwtToken(jwt)
                    userDataManager.saveUserData(jwt)

                    // Navigate to MainActivity
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } catch (e: IllegalArgumentException) {
                    // Show error to user
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun saveJwtToken(token: String) {
        // Save JWT token to SharedPreferences
        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("JWT_TOKEN", token)
            apply()
        }
    }
}