package com.example.cmpt362_stocksim.utils

data class UserJwtData(
    val uid: String,
    val username: String,
    val exp: Long
)
