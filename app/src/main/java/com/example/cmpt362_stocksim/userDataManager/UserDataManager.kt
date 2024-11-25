package com.example.cmpt362_stocksim.userDataManager

import android.content.Context
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.utils.JwtUtils

class UserDataManager(private val context: Context) {
    private val sharedPref = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE)
    private val backendRepository = BackendRepository()  // You'll need to handle dependency injection properly

    // Existing functions
    fun saveUserData(token: String) {
        sharedPref.edit().putString("JWT_TOKEN", token).apply()
        val userData = JwtUtils.decodeJwt(token)
        userData?.let {
            sharedPref.edit()
                .putString("USER_ID", it.uid)
                .putString("USERNAME", it.username)
                .apply()
        }
    }

    fun getUserId(): String? = sharedPref.getString("USER_ID", null)
    fun getUsername(): String? = sharedPref.getString("USERNAME", null)
    fun getJwtToken(): String? = sharedPref.getString("JWT_TOKEN", null)
    fun clearUserData() {
        sharedPref.edit().clear().apply()
    }

    // New functions
//    suspend fun getUserInfo(): UserInfo? {
//        return try {
//            val userId = getUserId() ?: return null
//            // Implement API call to /api/user/info?uid=USER_ID
//            // Return UserInfo object
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun getUserStocks(): UserStocks? {
//        return try {
//            val userId = getUserId() ?: return null
//            // Implement API call to /api/user/stocks?uid=USER_ID
//            // Return UserStocks object
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun getUserFavorites(): UserFavorites? {
//        return try {
//            val userId = getUserId() ?: return null
//            // Implement API call to /api/user/favorites?uid=USER_ID
//            // Return UserFavorites object
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun getUserTrades(): UserTrades? {
//        return try {
//            val userId = getUserId() ?: return null
//            // Implement API call to /api/user/trades?uid=USER_ID
//            // Return UserTrades object
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun getUserAchievements(): UserAchievements? {
//        return try {
//            val userId = getUserId() ?: return null
//            // Implement API call to /api/user/achievement?uid=USER_ID
//            // Return UserAchievements object
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun getUserNetWorth(): Double? {
//        return try {
//            val userId = getUserId() ?: return null
//            // Implement API call to /api/user/net_worth?uid=USER_ID
//            // Return net worth as Double
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    fun getCachedUserEmail(): String? = sharedPref.getString("USER_EMAIL", null)
//    fun getCachedUserBirthday(): String? = sharedPref.getString("USER_BIRTHDAY", null)
//    fun getCachedUserCash(): Double = sharedPref.getFloat("USER_CASH", 0f).toDouble()
//
//    fun updateCachedUserInfo(userInfo: UserInfo) {
//        sharedPref.edit()
//            .putString("USER_EMAIL", userInfo.email)
//            .putString("USER_BIRTHDAY", userInfo.birthday)
//            .putFloat("USER_CASH", userInfo.cash.toFloat())
//            .apply()
//    }
//
//    fun clearUserData() {
//        sharedPref.edit().clear().apply()
//    }
//
//    // Helper function to check if user is logged in
//    fun isLoggedIn(): Boolean = !getJwtToken().isNullOrEmpty()
}