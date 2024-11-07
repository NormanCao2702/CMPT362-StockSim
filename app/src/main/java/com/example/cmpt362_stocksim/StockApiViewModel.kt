package com.example.cmpt362_stocksim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class StockApiViewModel : ViewModel() {
    private val stockRepository = StockApiRepository()

    // Using LiveData to expose the data to the View (Activity)
    val stockData = liveData(Dispatchers.IO) {
        val data = stockRepository.getStockData()
        emit(data)
    }
}


