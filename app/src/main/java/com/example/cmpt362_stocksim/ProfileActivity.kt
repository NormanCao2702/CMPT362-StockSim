package com.example.cmpt362_stocksim

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    private lateinit var usernametv: TextView
    private lateinit var birthdaytv: TextView
    private lateinit var achievementNumberTv: TextView
    private lateinit var totalWorthTv: TextView
    private lateinit var stockInventoryTv: TextView

    private lateinit var addFriendButton: Button
    private lateinit var chatButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usernametv = findViewById(R.id.username_textview)
        birthdaytv = findViewById(R.id.user_birthday_textview)
        achievementNumberTv = findViewById(R.id.user_achievementnumber_textview)
        totalWorthTv = findViewById(R.id.user_totalworth_textview)
        stockInventoryTv = findViewById(R.id.user_stockinventory_textview)

        addFriendButton = findViewById(R.id.addFriendButton)
        chatButton = findViewById(R.id.chatFriendButton)



        addFriendButton.setOnClickListener{

        }

        chatButton.setOnClickListener{

        }


    }
}