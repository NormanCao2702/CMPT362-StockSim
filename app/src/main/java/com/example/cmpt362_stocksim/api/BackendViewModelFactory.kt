package com.example.cmpt362_stocksim.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BackendViewModelFactory(private val backendRepository: BackendRepository): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(BackendViewModel::class.java)) {
            return BackendViewModel(backendRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}