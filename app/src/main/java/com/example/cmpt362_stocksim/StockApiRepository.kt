package com.example.cmpt362_stocksim

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class StockApiRepository {

    private var client = HttpClient(CIO)
    private val apiKey = "bztYHvYoYC_nhrXcpEa3S4Q7SiFaCc6i"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getStockData(): StockResponsDataClass? = withContext(Dispatchers.IO) {
        val today = LocalDate.now()

        val date = when (today.dayOfWeek) {
            DayOfWeek.SATURDAY -> today.minusDays(1) // If it's Saturday, go back to Friday
            DayOfWeek.SUNDAY -> today.minusDays(2) // If it's Sunday, go back to Friday
            else -> today // Otherwise, use today's date
        }
        val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

        try {
            val response: HttpResponse = client.get("https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/$formattedDate?adjusted=true&apiKey=$apiKey")
            val responseBody = response.bodyAsText()

            return@withContext if (response.status.value == 200) {
                Gson().fromJson(responseBody, StockResponsDataClass::class.java)
            } else {
                null // Handle non-200 status
            }
        } catch (e: Exception) {
            println("Exception occurred: ${e.message}") // Print exception details
            null // Handle exception
        } finally {
            client.close()
        }
    }

    suspend fun getStockData2(tick: String): StockResponseDataClassInfo? = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.get("https://api.polygon.io/v3/reference/tickers/${tick}?apiKey=$apiKey")
            val responseBody = response.bodyAsText()

            return@withContext if (response.status.value == 200) {
                Gson().fromJson(responseBody, StockResponseDataClassInfo::class.java)
            } else {
                null // Handle non-200 status
            }
        } catch (e: Exception) {
            println("Exception occurred: ${e.message}") // Print exception details
            null // Handle exception
        } finally {
            client.close()
        }
    }

    suspend fun getStockData3(tick: String): StockResponseDataClassNews? = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.get("https://api.polygon.io/v2/reference/news?ticker=${tick}&limit=3&sort=published_utc&apiKey=$apiKey")
            val responseBody = response.bodyAsText()

            return@withContext if (response.status.value == 200) {
                Gson().fromJson(responseBody, StockResponseDataClassNews::class.java)
            } else {
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
