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
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
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

    private val userDataManager by lazy { UserDataManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        val userId = userDataManager.getUserId()

        lifecycleScope.launch {
            try {
                val response = userId?.let { backendViewModel.getUsersAchievement(it) }
                if (response != null) {
                    for (achievement in response.achievements){

                        if (achievement.id == 1){
                            val checkbox1 = findViewById<CheckBox>(R.id.achievement_checkbox)
                            val textv1 = findViewById<TextView>(R.id.achievement_item3)
                            checkbox1.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv1.text = "Unlocked: ${date}"
                        }
                        // DO OTHERS HERE ASWELL OR IN A CLASS FILE.
                        if (achievement.id == 2){
                            val checkbox2 = findViewById<CheckBox>(R.id.achievement_checkbox2)
                            val textv2 = findViewById<TextView>(R.id.achievement_item6)
                            checkbox2.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv2.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 3){
                            val checkbox3 = findViewById<CheckBox>(R.id.achievement_checkbox3)
                            val textv3 = findViewById<TextView>(R.id.achievement_item9)
                            checkbox3.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv3.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 4){
                            val checkbox4 = findViewById<CheckBox>(R.id.achievement_checkbox4)
                            val textv4 = findViewById<TextView>(R.id.achievement_item12)
                            checkbox4.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv4.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 5){
                            val checkbox5 = findViewById<CheckBox>(R.id.achievement_checkbox5)
                            val textv5 = findViewById<TextView>(R.id.achievement_item15)
                            checkbox5.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv5.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 6){
                            val checkbox6 = findViewById<CheckBox>(R.id.achievement_checkbox6)
                            val textv6 = findViewById<TextView>(R.id.achievement_item18)
                            checkbox6.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv6.text = "Unlocked: ${date}"
                        }

                        if (achievement.id == 7){
                            val checkbox7 = findViewById<CheckBox>(R.id.achievement_checkbox7)
                            val textv7 = findViewById<TextView>(R.id.achievement_item21)
                            checkbox7.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv7.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 8){
                            val checkbox8 = findViewById<CheckBox>(R.id.achievement_checkbox8)
                            val textv8 = findViewById<TextView>(R.id.achievement_item24)
                            checkbox8.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv8.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 9){
                            val checkbox9 = findViewById<CheckBox>(R.id.achievement_checkbox9)
                            val textv9 = findViewById<TextView>(R.id.achievement_item27)
                            checkbox9.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv9.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 10){
                            val checkbox10 = findViewById<CheckBox>(R.id.achievement_checkbox10)
                            val textv10 = findViewById<TextView>(R.id.achievement_item30)
                            checkbox10.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv10.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 11){
                            val checkbox11 = findViewById<CheckBox>(R.id.achievement_checkbox11)
                            val textv11 = findViewById<TextView>(R.id.achievement_item33)
                            checkbox11.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv11.text = "Unlocked: ${date}"
                        }
                        if (achievement.id == 12){
                            val checkbox12 = findViewById<CheckBox>(R.id.achievement_checkbox12)
                            val textv12 = findViewById<TextView>(R.id.achievement_item36)
                            checkbox12.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv12.text = "Unlocked: ${date}"
                        }

                        if (achievement.id == 15){
                            val checkbox15 = findViewById<CheckBox>(R.id.achievement_checkbox15)
                            val textv15 = findViewById<TextView>(R.id.achievement_item45)
                            checkbox15.isChecked = true
                            val date = formatEpochSeconds(achievement.date)
                            textv15.text = "Unlocked: ${date}"
                        }


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