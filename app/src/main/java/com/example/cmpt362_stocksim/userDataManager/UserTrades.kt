package com.example.cmpt362_stocksim.userDataManager

data class UserTrades(
    val received: List<Trade>,
    val sent: List<Trade>
)
