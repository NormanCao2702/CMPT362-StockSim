package com.example.cmpt362_stocksim.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class BackendViewModel(private val backendRepository: BackendRepository) : ViewModel() {

    suspend fun register(username: String, email: String, password: String, birthday: String): String  {
        return backendRepository.register(username, email, password, birthday)
    }
}