package com.example.cmpt362_stocksim

import androidx.lifecycle.ViewModel
import com.example.cmpt362_stocksim.BackendRepository.GetInfoResponse
import com.example.cmpt362_stocksim.BackendRepository.getEndorseResponse
import com.example.cmpt362_stocksim.BackendRepository.setEndorseResponse
import com.example.cmpt362_stocksim.BackendRepository.getHistoryEndorsement
import com.example.cmpt362_stocksim.BackendRepository.getPriceResponse
import com.example.cmpt362_stocksim.BackendRepository.getCashResponse
import com.example.cmpt362_stocksim.BackendRepository.getBuyResponse
import com.example.cmpt362_stocksim.BackendRepository.getSellResponse
import com.example.cmpt362_stocksim.BackendRepository.getInvResponse
import com.example.cmpt362_stocksim.BackendRepository.setUserAchResponse
import com.example.cmpt362_stocksim.BackendRepository.getUserAchResponse
import com.example.cmpt362_stocksim.BackendRepository.getAllAchResponse


class BackendViewModel(private val backendRepository: BackendRepository) : ViewModel() {

    suspend fun register(username: String, email: String, password: String, birthday: String): String  {
        return backendRepository.register(username, email, password, birthday)
    }

    suspend fun login(username: String, password: String): String {
        return backendRepository.login(username, password)
    }

    suspend fun getInfo(ticker: String): GetInfoResponse? {
        return backendRepository.getInfo(ticker)
    }
    suspend fun getEndorse(ticker: String): getEndorseResponse? {
        return backendRepository.getEndorse(ticker)
    }

    suspend fun setEndorse(ticker: String): setEndorseResponse? {
        return backendRepository.setEndorse(ticker)
    }

    suspend fun getHistory(ticker: String): getHistoryEndorsement? {
        return backendRepository.getHistory(ticker)
    }

    suspend fun getPrice(ticker: String): getPriceResponse? {
        return backendRepository.getPrice(ticker)
    }

    suspend fun getCash(user: String): getCashResponse? {
        return backendRepository.getCash(user)
    }

    suspend fun buyStock(ticker: String, amount: String): getBuyResponse? {
        return backendRepository.buyStock(ticker, amount)
    }

    suspend fun sellStock(ticker: String, amount: String): getSellResponse? {
        return backendRepository.sellStock(ticker, amount)
    }

    suspend fun getInv(user: String): getInvResponse? {
        return backendRepository.getInv(user)
    }

    suspend fun getUsersAchievement(user: String): getUserAchResponse? {
        return backendRepository.getUsersAchievement(user)
    }

    suspend fun setUsersAchievement(id: String): setUserAchResponse? {
        return backendRepository.setUsersAchievement(id)
    }

    suspend fun getAllAchievements(): getAllAchResponse? {
        return backendRepository.getAllAchievements()
    }

    suspend fun getUserInfo(userId: String): BackendRepository.UserInfoResponse {
        return backendRepository.getUserInfo(userId)
    }

}