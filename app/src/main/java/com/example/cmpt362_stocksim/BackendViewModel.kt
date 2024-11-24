package com.example.cmpt362_stocksim

import androidx.lifecycle.ViewModel
import com.example.cmpt362_stocksim.BackendRepository.GetInfoResponse
import com.example.cmpt362_stocksim.BackendRepository.getEndorseResponse
import com.example.cmpt362_stocksim.BackendRepository.setEndorseResponse
import com.example.cmpt362_stocksim.BackendRepository.getHistoryEndorsement
import com.example.cmpt362_stocksim.BackendRepository.getPriceResponse


class BackendViewModel(private val backendRepository: BackendRepository) : ViewModel() {

    suspend fun register(username: String, email: String, password: String, birthday: String): String  {
        return backendRepository.register(username, email, password, birthday)
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


}