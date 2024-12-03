package com.example.cmpt362_stocksim.ui.social.profile

import android.content.pm.ActivityInfo
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

/**
 * This activity is a users (not current user but other users) profile page
 */
class ProfileActivity : AppCompatActivity() {
    // Declare UI elements
    private lateinit var usernametv: TextView
    private lateinit var birthdaytv: TextView
    private lateinit var achievementLw: ListView
    private lateinit var totalWorthTv: TextView
    private lateinit var stockInventoryLw: ListView
    private lateinit var addFriendButton: Button
    private lateinit var removeFriendButton: Button
    private lateinit var chatButton: Button

    private lateinit var backendViewModel: BackendViewModel

    // Lazy initialization for user data manager
    private val userDataManager by lazy { UserDataManager(this) }
    private lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize UI elements
        usernametv = findViewById(R.id.username_textview)
        birthdaytv = findViewById(R.id.user_birthday_textview)
        achievementLw = findViewById(R.id.user_achievementnumber_textview)
        totalWorthTv = findViewById(R.id.user_totalworth_textview)
        stockInventoryLw = findViewById(R.id.user_stockinventory_textview)

        addFriendButton = findViewById(R.id.addFriendButton)
        removeFriendButton = findViewById(R.id.removeFriendButton)
        chatButton = findViewById(R.id.chatFriendButton)

        // Get the user ID from the Intent extras
        uid = intent.extras?.getInt("USER_ID").toString()
        backendViewModel = BackendViewModelFactory(BackendRepository()).create(BackendViewModel::class.java)

        // Add Friend Button logic
        addFriendButton.setOnClickListener{
            val token = userDataManager.getJwtToken()
            lifecycleScope.launch {
                try {
                    backendViewModel.setFriendRequest(uid, token!!)
                    Toast.makeText(this@ProfileActivity, "Friend request sent!", Toast.LENGTH_LONG).show()
                }catch(e: IllegalArgumentException) {
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
        // Remove Friend Button logic
        removeFriendButton.setOnClickListener {
            val token = userDataManager.getJwtToken()
            lifecycleScope.launch {
                try {
                    backendViewModel.setRemove(uid, token!!)
                    Toast.makeText(this@ProfileActivity, "Friend removed!", Toast.LENGTH_LONG).show()
                }catch(e: IllegalArgumentException) {
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }


        // Check if you're looking at yourself
        if(userDataManager.getUserId()!! == uid) {
            addFriendButton.visibility = View.GONE
        } else {
            // Check if users are friends, if so remove the add button
            lifecycleScope.launch {
                val token = userDataManager.getJwtToken()!!
                if(backendViewModel.getIsUserFriend(uid, token)) {
                    runOnUiThread {
                        addFriendButton.visibility = View.GONE
                        removeFriendButton.visibility = View.VISIBLE
                    }
                }
            }
        }

        // Initialize adapters for stock inventory and achievements
        val stockAdapter = StockListAdapter(this, ArrayList())
        stockInventoryLw.adapter = stockAdapter
        val achievementAdapater = AchievementListAdapter(this, ArrayList())
        achievementLw.adapter = achievementAdapater

        // Fetch user information, stocks, and achievements
        lifecycleScope.launch {
            // Fetch user info
            val userInfo = backendViewModel.getUserInfo(uid)
            // Format user's net worth as currency
            val currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setMaximumFractionDigits(2);
            currencyFormat.setCurrency(Currency.getInstance("USD"));

            // Update UI with user info
            Handler(Looper.getMainLooper()).post {

                usernametv.text = userInfo.username
                birthdaytv.text = userInfo.birthday
                totalWorthTv.text = currencyFormat.format(userInfo.net_worth)
            }
            // Fetch and display user's stock inventory
            val stocks = backendViewModel.getInv(uid)
            Handler(Looper.getMainLooper()).post {
                stockAdapter.replace(stocks!!.stocks)
                stockAdapter.notifyDataSetChanged()
                stockInventoryLw.invalidateViews()
            }
            // Fetch and display user's achievements
            val achievements = backendViewModel.getUsersAchievement(uid)!!
            Handler(Looper.getMainLooper()).post {
                achievementAdapater.replace(achievements.achievements)
                achievementAdapater.notifyDataSetChanged()
                achievementLw.invalidateViews()
            }
        }
        setTitle("User")
    }
}