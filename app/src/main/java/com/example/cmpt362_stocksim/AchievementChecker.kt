package com.example.cmpt362_stocksim

import android.content.Context
import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.cmpt362_stocksim.utils.JwtUtils
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext

class AchievementChecker(private val context: Context, private val lifecycleOwner: LifecycleOwner) {
    private val backendViewModel: BackendViewModel
    private val userDataManager by lazy { UserDataManager(context)}
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    //private var isAchievementUnlocked1 = false
    //private var isAchievementUnlocked2 = false
   // private var isAchievementUnlocked3 = false
//    private var isAchievementUnlocked4 = false
//    private var isAchievementUnlocked5 = false
//    private var isAchievementUnlocked6 = false
//    private var isAchievementUnlocked7 = false
//    private var isAchievementUnlocked8 = false
//    private var isAchievementUnlocked9 = false
//    private var isAchievementUnlocked10 = false
//    private var isAchievementUnlocked11 = false
//    private var isAchievementUnlocked12 = false
//    private var isAchievementUnlocked13 = false

    private var setupFinished = false

    init {
        val repository = BackendRepository()
        val viewModelFactory = BackendViewModelFactory(repository)
        backendViewModel = viewModelFactory.create(BackendViewModel::class.java)
        sharedPreferences.edit().putBoolean("hasSetup", false).apply()
    }


    fun setupChecker(){
        sharedPreferences.edit().putBoolean("settingUpDone", false).apply()

        // Set everything to false
        sharedPreferences.edit().putBoolean("hasSetup", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked1", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked2", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked3", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked4", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked5", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked6", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked7", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked8", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked9", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked10", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked11", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked12", false).apply()
        sharedPreferences.edit().putBoolean("isAchievementUnlocked13", false).apply()
        sharedPreferences.edit().putFloat("stockCount2", 0.0F).apply()
        sharedPreferences.edit().putFloat("stockCount3", 0.0F).apply()


        // Set the ones we have to true

        val userId = userDataManager.getUserId()

        lifecycleOwner.lifecycleScope.launch {
            try {
                val response = userId?.let { backendViewModel.getUsersAchievement(it) }
                if (response != null) {
                    for (achievement in response.achievements){
                        if (achievement.id == 1){
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked1", true).apply()
                        } else if (achievement.id == 2) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked2", true).apply()
                        } else if (achievement.id == 3) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked3", true).apply()
                        } else if (achievement.id == 4) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked4", true).apply()
                        } else if (achievement.id == 5) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked5", true).apply()
                        } else if (achievement.id == 6) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked6", true).apply()
                        } else if (achievement.id == 7) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked7", true).apply()
                        } else if (achievement.id == 8) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked8", true).apply()
                        } else if (achievement.id == 9) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked9", true).apply()
                        } else if (achievement.id == 10) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked10", true).apply()
                        } else if (achievement.id == 11) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked11", true).apply()
                        } else if (achievement.id == 12) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked12", true).apply()
                        } else if (achievement.id == 15) {
                            sharedPreferences.edit().putBoolean("isAchievementUnlocked13", true).apply()
                        } else {
                            // Do nothing
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            try {
                var count = 0.0F
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    for (stock in response.stocks) {
                        count += stock.amount.toFloat()
                    }
                }
                sharedPreferences.edit().putFloat("stockCount", count).apply()
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }

        sharedPreferences.edit().putBoolean("settingUpDone", true).apply()
    }


    fun checkForAchievements(){
        checkForAchievementOne()
        checkForAchievementTwo()
        checkForAchievementThree()
        checkForAchievementFour()
        checkForAchievementFive()
        checkForAchievementSix()

      //  checkForAchievementSeven()
       // checkForAchievementEight()
       // checkForAchievementNine()
      //  checkForAchievementTen()
      //  checkForAchievementEleven()
      //  checkForAchievementTweleve()
      // checkForAchievementThirteen()

    }

    private fun checkForAchievementSix() {
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked6 = sharedPreferences.getBoolean("isAchievementUnlocked6", false)
        // If its already unlocked dont check it
        if (isAchievementUnlocked6 == true){
            return
        }

        //Social Broker  add a user to friend list <- check if friendlist size is bigger than 0..



    }

    private fun checkForAchievementFive() {
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked5 = sharedPreferences.getBoolean("isAchievementUnlocked5", false)
        // If its already unlocked dont check it
        if (isAchievementUnlocked5 == true){
            return
        }

        GlobalScope.launch {
            try {
                var count = 0.0F
                var flag = false
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    for (stock in response.stocks) {
                        if(stock.amount.toFloat() >= 25){
                            flag = true
                        }
                    }
                }

                val isAchievementUnlocked55 = sharedPreferences.getBoolean("isAchievementUnlocked5", false)

                if (flag == true && isAchievementUnlocked55 == false) {
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked5", true).apply()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Bulk Buyer Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("5",
                                    it
                                )
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.d("MJR", e.message!!)
                        }
                    }
                }

            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)

            }
        }

    }



    private fun checkForAchievementFour() {
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked4 = sharedPreferences.getBoolean("isAchievementUnlocked4", false)
        // If its already unlocked dont check it
        if (isAchievementUnlocked4 == true){
            return
        }

        GlobalScope.launch {
            try {
                var count = 0
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    for (stock in response.stocks) {
                        count += 1
                    }
                }

                val isAchievementUnlocked44 = sharedPreferences.getBoolean("isAchievementUnlocked4", false)

                if (count >= 5 && isAchievementUnlocked44 == false) {
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked4", true).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Variety Shopper Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("4",
                                    it
                                )
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.d("MJR", e.message!!)
                        }
                    }
                }

            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)

            }
        }
    }


    private fun checkForAchievementThree() {
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked3 = sharedPreferences.getBoolean("isAchievementUnlocked3", false)
        // If its already unlocked dont check it
        if (isAchievementUnlocked3 == true){
            return
        }

        val netWorth = userDataManager.getNetWorth()

        if(netWorth >= 11000.00 && isAchievementUnlocked3 == false){
            sharedPreferences.edit().putBoolean("isAchievementUnlocked3", true).apply()


            GlobalScope.launch {
                try {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Five Figures Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val token = userDataManager.getJwtToken()
                    val response = token?.let {
                        backendViewModel.setUsersAchievement("3",
                            it
                        )
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
            }
        }
    }



    fun checkForAchievementTwo() {
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked2 = sharedPreferences.getBoolean("isAchievementUnlocked2", false)
        // If its already unlocked dont check it
        if (isAchievementUnlocked2 == true){
            return
        }

        GlobalScope.launch {
            try {
                var count = 0.0F
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    for (stock in response.stocks) {
                        count += stock.amount.toFloat()
                    }
                }
                val startStocks = sharedPreferences.getFloat("stockCount", 0.0F)
                val startStocks2 = sharedPreferences.getFloat("stockCount2", 0.0F)
                val startStocks3 = sharedPreferences.getFloat("stockCount3", 0.0F)

                val isAchievementUnlocked2 = sharedPreferences.getBoolean("isAchievementUnlocked2", false)
                if ((count == (startStocks-5.0F) && isAchievementUnlocked2 == false) || ((startStocks3 >= 5.0F) && isAchievementUnlocked2 == false)){
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked2", true).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Cashing Out Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("2",
                                    it
                                )
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.d("MJR", e.message!!)
                        }
                    }
                }

            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }
    }


    fun checkForAchievementOne(){
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }
        val isAchievementUnlocked1 = sharedPreferences.getBoolean("isAchievementUnlocked1", false)

        // If its already unlocked dont check it
        if (isAchievementUnlocked1 == true){
            return
        }

        GlobalScope.launch {
            try {
                var count = 0.0F
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    for (stock in response.stocks) {

                        count += stock.amount.toFloat()
                    }
                }

                val isAchievementUnlocked1 = sharedPreferences.getBoolean("isAchievementUnlocked1", false)
                if (count >= 5.0 && isAchievementUnlocked1 == false) {
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked1", true).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Beginner Investor Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("1",
                                    it
                                )
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.d("MJR", e.message!!)
                        }
                    }
                }

            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)

            }
        }
    }
}