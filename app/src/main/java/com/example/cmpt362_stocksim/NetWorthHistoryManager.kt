package com.example.cmpt362_stocksim

import android.content.Context
import android.util.Log
import kotlin.math.abs

// Create a NetWorthHistoryManager to handle CSV operations
class NetWorthHistoryManager(private val context: Context) {
    private val fileName = "networth_history.csv"

    data class NetWorthEntry(
        val timestamp: Long,
        val value: Double
    )

    fun saveNetWorthValue(value: Double) {
        try {
            val history = getNetWorthHistory()

            // Check if value has changed from last entry
            if (history.isNotEmpty()) {
                val lastEntry = history.maxByOrNull { it.timestamp }!!
                if (abs(lastEntry.value - value) < 0.01) {
                    Log.d("NetWorthDebug", "Value hasn't changed significantly, skipping save")
                    return
                }
            }

            val entry = "${System.currentTimeMillis()},${value}\n"
            Log.d("NetWorthDebug", "Saving new value: $value")

            context.openFileOutput(fileName, Context.MODE_APPEND).use { stream ->
                stream.write(entry.toByteArray())
                stream.flush()
            }

            // Keep only last 30 entries
            cleanupOldEntries()

        } catch (e: Exception) {
            Log.e("NetWorthDebug", "Error saving net worth: ${e.message}", e)
            throw e
        }
    }

    private fun cleanupOldEntries() {
        val history = getNetWorthHistory()
        if (history.size > 30) {
            val sortedHistory = history.sortedByDescending { it.timestamp }
            val keepEntries = sortedHistory.take(30)

            // Rewrite file with only kept entries
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { stream ->
                keepEntries.forEach { entry ->
                    stream.write("${entry.timestamp},${entry.value}\n".toByteArray())
                }
            }
        }
    }

    fun getNetWorthHistory(): List<NetWorthEntry> {
        return try {
            if (!context.getFileStreamPath(fileName).exists()) {
                Log.d("NetWorthDebug", "History file doesn't exist yet")
                return emptyList()
            }

            context.openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.mapNotNull { line ->
                    try {
                        val (timestamp, value) = line.split(",")
                        NetWorthEntry(timestamp.toLong(), value.toDouble())
                    } catch (e: Exception) {
                        Log.e("NetWorthDebug", "Error parsing line: $line", e)
                        null
                    }
                }.toList()
            }.also { entries ->
                Log.d("NetWorthDebug", "Read ${entries.size} entries from history")
                entries.forEach { entry ->
                    Log.d("NetWorthDebug", "Entry: time=${entry.timestamp}, value=${entry.value}")
                }
            }
        } catch (e: Exception) {
            Log.e("NetWorthDebug", "Error reading history: ${e.message}", e)
            emptyList()
        }
    }

    fun clearHistory() {
        context.deleteFile(fileName)
    }
}