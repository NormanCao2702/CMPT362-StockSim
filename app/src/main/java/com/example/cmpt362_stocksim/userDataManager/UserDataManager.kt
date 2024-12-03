package com.example.cmpt362_stocksim.userDataManager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.utils.JwtUtils
import java.io.ByteArrayOutputStream
import android.util.Base64


class UserDataManager(private val context: Context) {
    private val sharedPref = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE)
    private val backendViewModel: BackendViewModel  // You'll need to handle dependency injection properly

    init {
        val repository = BackendRepository()
        val viewModelFactory = BackendViewModelFactory(repository)
        backendViewModel = viewModelFactory.create(BackendViewModel::class.java)
    }

    // For login/register - saves JWT data
    fun saveUserData(token: String) {
        // Save JWT token
        sharedPref.edit().putString("JWT_TOKEN", token).apply()

        // Decode and save user data from JWT
        val userData = JwtUtils.decodeJwt(token)
        userData?.let {
            sharedPref.edit()
                .putString("USER_ID", it.uid)
                .putString("USERNAME", it.username)
                .apply()
        }
    }

    // For additional user info
    fun saveUserInfo(email: String, birthday: String, netWorth: Double) {
        sharedPref.edit()
            .putString("USER_EMAIL", email)
            .putString("USER_BIRTHDAY", birthday)
            .putString("USER_NET_WORTH", netWorth.toString())
            .apply()
    }

    fun saveProfileImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

        sharedPref.edit()
            .putString("PROFILE_IMAGE", base64Image)
            .apply()
    }

    fun getUserId(): String? = sharedPref.getString("USER_ID", null)
    fun getUsername(): String? = sharedPref.getString("USERNAME", null)
    fun getJwtToken(): String? = sharedPref.getString("JWT_TOKEN", null)
    fun getEmail(): String? = sharedPref.getString("USER_EMAIL", null)
    fun getBirthday(): String? = sharedPref.getString("USER_BIRTHDAY", null)
    fun getNetWorth(): Double = sharedPref.getString("USER_NET_WORTH", "0.0")?.toDoubleOrNull() ?: 0.0

    // Refresh user info from API
    suspend fun refreshUserInfo(): Boolean {
        return try {
            val userId = getUserId() ?: return false
            val userInfo = backendViewModel.getUserInfo(userId)
            saveUserInfo(userInfo.email, userInfo.birthday, userInfo.net_worth)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getProfileImage(): Bitmap? {
        val base64Image = sharedPref.getString("PROFILE_IMAGE", null)
        return if (base64Image != null) {
            try {
                val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun clearUserData() {
        sharedPref.edit().clear().apply()
    }
}