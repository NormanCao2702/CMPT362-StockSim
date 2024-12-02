package com.example.cmpt362_stocksim

import android.content.Context
import android.util.Log
import kotlin.math.abs

// Create a NetWorthHistoryManager to handle CSV operations
class NetWorthHistoryManager(private val context: Context) {
    private fun getFileName(userId: String): String {
        return "networth_history_${userId}.csv"
    }

    data class NetWorthEntry(
        val timestamp: Long,
        val value: Double
    )

    // Clear old user's data when logging out
    fun clearUserHistory(userId: String) {
        context.deleteFile(getFileName(userId))
    }

    fun saveNetWorthValue(userId: String, value: Double) {
        Log.d("ChartDebug", "Saving net worth for user $userId: $value")
        val entry = "${System.currentTimeMillis()},${value}\n"
        context.openFileOutput(getFileName(userId), Context.MODE_APPEND).use {
            it.write(entry.toByteArray())
        }
    }

    fun getNetWorthHistory(userId: String): List<NetWorthEntry> {
        return try {
            context.openFileInput(getFileName(userId)).bufferedReader().useLines { lines ->
                lines.map { line ->
                    val (timestamp, value) = line.split(",")
                    NetWorthEntry(timestamp.toLong(), value.toDouble())
                }.toList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}