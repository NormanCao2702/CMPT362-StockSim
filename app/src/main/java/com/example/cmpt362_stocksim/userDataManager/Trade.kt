package com.example.cmpt362_stocksim.userDataManager

data class Trade(
    val symbol: String,
    val amount: Int,
    val price: Double,
    val date: Long
)
