package com.example.cmpt362_stocksim

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Date
import java.util.Locale


class AchievementActivity : AppCompatActivity() {

    val repository2 = BackendRepository()
    val viewModelFactory2 = BackendViewModelFactory(repository2)
    val backendViewModel = viewModelFactory2.create(BackendViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)



        lifecycleScope.launch {
            try {
                val response = backendViewModel.getUsersAchievement("15")
                if (response != null) {
                    for (achievement in response.achievements){

                        if (achievement.id == 1){
                            val checkbox1 = findViewById<CheckBox>(R.id.achievement_checkbox)
                            val textv1 = findViewById<TextView>(R.id.achievement_item3)
                            checkbox1.isChecked = true
                            println("AHHHH")
                            println(achievement.date)
                            val date = formatEpochSeconds(achievement.date)
                            textv1.text = "Unlocked: ${date}"
                        }
                        // DO OTHERS HERE ASWELL OR IN A CLASS FILE.
                    }
                }
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }

        }

        findViewById<ImageButton>(R.id.btnBack2).setOnClickListener {
            finish()
        }
    }


    fun formatEpochSeconds(epochSeconds: Long): String {
        val date = Date(epochSeconds * 1000) // Convert seconds to milliseconds
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)
    }
}