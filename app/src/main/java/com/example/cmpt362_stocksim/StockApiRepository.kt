package com.example.cmpt362_stocksim

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockApiRepository {

    private val client = HttpClient(CIO)
    private val apiKey = "Tu1DQmHcL8OjisY7x4_pKp6d1ftedQG1"

    suspend fun getStockData(): StockResponsDataClass? = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.get("https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/2023-01-09?adjusted=true&apiKey=$apiKey")
            val responseBody = response.bodyAsText()

            return@withContext if (response.status.value == 200) {
                println("IM HERE WTF222")
                Gson().fromJson(responseBody, StockResponsDataClass::class.java)
            } else {
                println("IM HERE WTF")
                null // Handle non-200 status
            }
        } catch (e: Exception) {
            println("Exception occurred: ${e.message}") // Print exception details
            null // Handle exception
        } finally {
            client.close()
        }
    }

}