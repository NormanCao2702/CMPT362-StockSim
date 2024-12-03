package com.example.cmpt362_stocksim.ui.achievement

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext

/**
 * This file checks for the users achievements and sets them accordingly
 */
class AchievementChecker(private val context: Context, private val lifecycleOwner: LifecycleOwner) {

    // Setup backend and sharepref
    private val backendViewModel: BackendViewModel
    private val userDataManager by lazy { UserDataManager(context)}
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    init {
        val repository = BackendRepository()
        val viewModelFactory = BackendViewModelFactory(repository)
        backendViewModel = viewModelFactory.create(BackendViewModel::class.java)
        sharedPreferences.edit().putBoolean("hasSetup", false).apply()
    }

    // Sets up the achievement checker for each new user login
    fun setupChecker(){
        // Set all sharepref fields to false
        sharedPreferences.edit().putBoolean("settingUpDone", false).apply()
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

        // Set the achievements the user has to true
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

        // Set a count for the users current stock amount
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
        // Set sharepref boolean to true indicating that the setup is one
        sharedPreferences.edit().putBoolean("settingUpDone", true).apply()
    }

    // This function calls all the achievement checks
    fun checkForAchievements(){
        checkForAchievementOne()
        checkForAchievementTwo()
        checkForAchievementThree()
        checkForAchievementFour()
        checkForAchievementFive()
        checkForAchievementSix()
        checkForAchievementSeven()
        checkForAchievementEight()
        checkForAchievementNine()
        checkForAchievementTen()
        checkForAchievementEleven()
        checkForAchievementTweleve()
        checkForAchievementThirteen()
    }

    // This function checks for the achievement Six Figures 100k total value, if user has it then set it
    private fun checkForAchievementThirteen() {
        // Check if setup is done
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        // If its already unlocked dont check it
        val isAchievementUnlocked13 = sharedPreferences.getBoolean("isAchievementUnlocked13", false)
        if (isAchievementUnlocked13 == true){
            return
        }

        // Check if user earned achievement
        val netWorth = userDataManager.getNetWorth()
        if(netWorth >= 100000.00 && isAchievementUnlocked13 == false){
            // Set to true and give user achievement if they earned it
            sharedPreferences.edit().putBoolean("isAchievementUnlocked13", true).apply()
            GlobalScope.launch {
                try {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Six Figures Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val token = userDataManager.getJwtToken()
                    val response = token?.let {
                        backendViewModel.setUsersAchievement("15",
                            it
                        )
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
            }
        }
    }

    // This function checks for the Diversification achievement , if user has it then set it
    private fun checkForAchievementTweleve() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked12 = sharedPreferences.getBoolean("isAchievementUnlocked12", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked12 == true){
            return
        }

        // Check if user earned achievement
        GlobalScope.launch {
            try {
                var count = 0
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    // Count the number of different stocks
                    for (stock in response.stocks) {
                        count += 1
                    }
                }

                // If the user has 20 or more different stocks and the achievement is not unlocked, unlock it
                val isAchievementUnlocked1212 = sharedPreferences.getBoolean("isAchievementUnlocked12", false)
                if (count >= 20 && isAchievementUnlocked1212 == false) {
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked12", true).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Diversification Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("12",
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

    // This function checks for the Big Player achievement, if the user has it, then set it
    private fun checkForAchievementEleven() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked11 = sharedPreferences.getBoolean("isAchievementUnlocked11", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked11 == true){
            return
        }

        val netWorth = userDataManager.getNetWorth()

        // Check if the user has a net worth greater than or equal to 45,000 to unlock the achievement
        if(netWorth >= 45000.00 && isAchievementUnlocked11 == false){
            // Set to true and give user the achievement
            sharedPreferences.edit().putBoolean("isAchievementUnlocked11", true).apply()
            GlobalScope.launch {
                try {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Big Player Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val token = userDataManager.getJwtToken()
                    val response = token?.let {
                        backendViewModel.setUsersAchievement("11",
                            it
                        )
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
            }
        }
    }


    // This function checks for the Cash King achievement, if the user has it, then set it
    private fun checkForAchievementTen() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }
        val isAchievementUnlocked10 = sharedPreferences.getBoolean("isAchievementUnlocked10", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked10 == true){
            return
        }

        // Check if user can receive cash king achievement
        GlobalScope.launch {
            try {
                var count = 0
                var flag = false
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getCash(it) }
                if (response != null) {
                    if(response.cash >= 25000.00F){
                        flag = true
                    }
                }

                // If the user has 25k or more in cash and the achievement is not unlocked, unlock it
                val isAchievementUnlocked1010 = sharedPreferences.getBoolean("isAchievementUnlocked10", false)
                if (flag == true && isAchievementUnlocked1010 == false) {
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked10", true).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Cash King Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("10",
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

    // This function checks for the Seasoned Trader achievement, if the user has it, then set it
    private fun checkForAchievementNine() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked9 = sharedPreferences.getBoolean("isAchievementUnlocked9", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked9 == true){
            return
        }

        GlobalScope.launch {
            try {
                var count = 0.0F
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                // Calculate the total amount of stocks held
                if (response != null) {
                    for (stock in response.stocks) {
                        count += stock.amount.toFloat()
                    }
                }
                val startStocks = sharedPreferences.getFloat("stockCount", 0.0F)
                val startStocks2 = sharedPreferences.getFloat("stockCount2", 0.0F)
                val startStocks3 = sharedPreferences.getFloat("stockCount3", 0.0F)


                // Check if the achievement condition is met (either stock amount matches or a secondary condition is met)
                val isAchievementUnlocked99 = sharedPreferences.getBoolean("isAchievementUnlocked9", false)
                if ((count == (startStocks-50.0F) && isAchievementUnlocked99 == false) || ((startStocks3 >= 50.0F) && isAchievementUnlocked99 == false)){
                    // Set the achievement for the user and toast
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked9", true).apply()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Seasoned Trader Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("9",
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

    // This function checks for the Quarter Milestone achievement, if the user has it, then set it
    private fun checkForAchievementEight() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked8 = sharedPreferences.getBoolean("isAchievementUnlocked8", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked8 == true){
            return
        }

        val netWorth = userDataManager.getNetWorth()

        // Check if the user has a net worth greater than or equal to 25,000 to unlock the achievement
        if(netWorth >= 25000.00 && isAchievementUnlocked8 == false){

            // Give user the achievement and toast
            sharedPreferences.edit().putBoolean("isAchievementUnlocked8", true).apply()
            GlobalScope.launch {
                try {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Quarter Milestone Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val token = userDataManager.getJwtToken()
                    val response = token?.let {
                        backendViewModel.setUsersAchievement("8",
                            it
                        )
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
            }
        }
    }

    // This function checks for the Portfolio Builder achievement, if the user has it, then set it
    private fun checkForAchievementSeven() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked7 = sharedPreferences.getBoolean("isAchievementUnlocked7", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked7 == true){
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

                // If the user has 10 or more stocks and the achievement is not unlocked, unlock it
                val isAchievementUnlocked77 = sharedPreferences.getBoolean("isAchievementUnlocked7", false)
                if (count >= 10 && isAchievementUnlocked77 == false) {

                    // Give user the achievement and toast
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked7", true).apply()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Portfolio Builder Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("7",
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

    // This function checks for the Social Broker achievement, if the user has it, then set it
    private fun checkForAchievementSix() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked6 = sharedPreferences.getBoolean("isAchievementUnlocked6", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked6 == true){
            return
        }

        GlobalScope.launch {
            try {
                var flag = false
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getFriends(it) }
                if (response != null) {
                    // Check if the user has at least one friend
                    if (response.friends.size >= 1){
                        flag = true
                    }
                }

                val isAchievementUnlocked66 = sharedPreferences.getBoolean("isAchievementUnlocked6", false)
                if (flag == true && isAchievementUnlocked66 == false) {
                    // Give user the achievement and toast it
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked6", true).apply()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Social Broker Achievement Unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalScope.launch {
                        try {
                            val token = userDataManager.getJwtToken()
                            val response = token?.let {
                                backendViewModel.setUsersAchievement("6",
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

    // This function checks for the Bulk Buyer achievement, if the user has it, then set it
    private fun checkForAchievementFive() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked5 = sharedPreferences.getBoolean("isAchievementUnlocked5", false)
        // If the achievement is already unlocked, don't check it again
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
                        // Check if the user holds at least 25 units of any stock
                        if(stock.amount.toFloat() >= 25){
                            flag = true
                        }
                    }
                }
                val isAchievementUnlocked55 = sharedPreferences.getBoolean("isAchievementUnlocked5", false)
                if (flag == true && isAchievementUnlocked55 == false) {
                    // Give achievement to user and toast
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

    // This function checks for the Variety Shopper achievement, if the user has it, then set it
    private fun checkForAchievementFour() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked4 = sharedPreferences.getBoolean("isAchievementUnlocked4", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked4 == true){
            return
        }

        GlobalScope.launch {
            try {
                var count = 0
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    // Count the number of stocks in the user's inventory
                    for (stock in response.stocks) {
                        count += 1
                    }
                }

                // If stocks are greater than 5 then give achievement and toast
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

    // This function checks for the Five Figures achievement (net worth >= 11,000), if the user has it, then set it
    private fun checkForAchievementThree() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked3 = sharedPreferences.getBoolean("isAchievementUnlocked3", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked3 == true){
            return
        }

        val netWorth = userDataManager.getNetWorth()

        // Check if user has over 11,000
        if(netWorth >= 11000.00 && isAchievementUnlocked3 == false){
            // Give user the achievement and toast
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

    // This function checks for the Cashing Out achievement based on stock count, if the user has it, then set it
    fun checkForAchievementTwo() {
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }

        val isAchievementUnlocked2 = sharedPreferences.getBoolean("isAchievementUnlocked2", false)
        // If the achievement is already unlocked, don't check it again
        if (isAchievementUnlocked2 == true){
            return
        }

        GlobalScope.launch {
            try {
                var count = 0.0F
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    // check user currnet stocks
                    for (stock in response.stocks) {
                        count += stock.amount.toFloat()
                    }
                }
                val startStocks = sharedPreferences.getFloat("stockCount", 0.0F)
                val startStocks2 = sharedPreferences.getFloat("stockCount2", 0.0F)
                val startStocks3 = sharedPreferences.getFloat("stockCount3", 0.0F)

                // if users current stock is -5 then give achievement and toast
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

    // This function checks for the Beginner Investor achievement based on stock count, if the user has it, then set it
    fun checkForAchievementOne(){
        // If the setup is not done, exit the function
        val setupDone = sharedPreferences.getBoolean("settingUpDone", false)
        if(setupDone == false){
            return
        }
        val isAchievementUnlocked1 = sharedPreferences.getBoolean("isAchievementUnlocked1", false)

        // If the achievement is already unlocked, don't check it again
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
                // Check if the stock count is greater than or equal to 5
                val isAchievementUnlocked1 = sharedPreferences.getBoolean("isAchievementUnlocked1", false)
                if (count >= 5.0 && isAchievementUnlocked1 == false) {

                    // Give user the achievement and toast
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