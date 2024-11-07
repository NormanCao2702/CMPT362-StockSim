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


    // Dynamically pass ticker string as a parameter to get stock data for that ticker
    fun getStockData2(ticker: String) = liveData(Dispatchers.IO) {
        println("Testing Ticker: ${ticker}")
        val data = stockRepository.getStockData2(ticker)  // Pass ticker dynamically

        println("Past the data")

        emit(data)
    }

}

