package com.example.cmpt362_stocksim.ui.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PortfolioViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is portfolio Fragment"
    }
    val text: LiveData<String> = _text
}