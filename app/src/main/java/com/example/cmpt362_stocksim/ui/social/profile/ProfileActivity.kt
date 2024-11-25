package com.example.cmpt362_stocksim.ui.social.profile

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.BackendViewModel
import com.example.cmpt362_stocksim.BackendViewModelFactory
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var usernametv: TextView
    private lateinit var birthdaytv: TextView
    private lateinit var achievementLw: ListView
    private lateinit var totalWorthTv: TextView
    private lateinit var stockInventoryLw: ListView

    private lateinit var addFriendButton: Button
    private lateinit var chatButton: Button

    private lateinit var backendViewModel: BackendViewModel

    private val userDataManager by lazy { UserDataManager(this) }
    private lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usernametv = findViewById(R.id.username_textview)
        birthdaytv = findViewById(R.id.user_birthday_textview)
        achievementLw = findViewById(R.id.user_achievementnumber_textview)
        totalWorthTv = findViewById(R.id.user_totalworth_textview)
        stockInventoryLw = findViewById(R.id.user_stockinventory_textview)

        addFriendButton = findViewById(R.id.addFriendButton)
        chatButton = findViewById(R.id.chatFriendButton)
        uid = intent.extras?.getInt("USER_ID").toString()
        backendViewModel = BackendViewModelFactory(BackendRepository()).create(BackendViewModel::class.java)


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
        val stockAdapter = StockListAdapter(this, ArrayList())
        stockInventoryLw.adapter = stockAdapter
        val achievementAdapater = AchievementListAdapter(this, ArrayList())
        achievementLw.adapter = achievementAdapater

        //chatButton.setOnClickListener{

        //}

        lifecycleScope.launch {
            val userInfo = backendViewModel.getUserInfo(uid)
            val currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setMaximumFractionDigits(2);
            currencyFormat.setCurrency(Currency.getInstance("USD"));

            Handler(Looper.getMainLooper()).post {

                usernametv.text = userInfo.username
                birthdaytv.text = userInfo.birthday
                totalWorthTv.text = currencyFormat.format(userInfo.net_worth)
            }

            val stocks = backendViewModel.getInv(uid)
            Handler(Looper.getMainLooper()).post {
                stockAdapter.replace(stocks!!.stocks)
                stockAdapter.notifyDataSetChanged()
                stockInventoryLw.invalidateViews()
            }

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