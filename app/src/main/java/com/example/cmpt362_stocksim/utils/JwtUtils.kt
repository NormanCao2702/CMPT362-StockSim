package com.example.cmpt362_stocksim.utils

import com.auth0.android.jwt.JWT

class JwtUtils {
    companion object {
        fun decodeJwt(token: String): UserJwtData? {
            return try {
                val jwt = JWT(token)
                UserJwtData(
                    uid = jwt.getClaim("uid").asString() ?: "",
                    username = jwt.getClaim("username").asString() ?: "",
                    exp = jwt.getClaim("exp").asLong() ?: 0L
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}