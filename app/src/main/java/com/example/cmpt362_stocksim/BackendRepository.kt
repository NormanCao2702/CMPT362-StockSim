package com.example.cmpt362_stocksim

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class BackendRepository {
    private val HOST = "stocksim.breadmod.info"
    private val API_PATH = "api"
    private val client = HttpClient(CIO)
    private val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzIxMTI5OTgsImV4cCI6MTczNzI5Njk5OCwidWlkIjoxNSwidXNlcm5hbWUiOiJhZG1pbiJ9.RNaWkEsx0UlrgbxqZirG1xXz7CfMa5hhXyjCLNmPGHo"

    data class ErrorResponse(val error: String)
    data class RegisterResponse(val token: String)

    data class GetInfoResponse(
        val name: String,
        val postal_code: String,
        val address: String,
        val city: String,
        val state: String,
        val description: String,
        val homepage: String,
        val icon_url: String,
        val logo_url: String,
        val market_cap: String,
        val list_date: String,
        val phone_number: String,
        val employee_count: String
    )

    data class getEndorseResponse(val endorsements: Int)
    data class setEndorseResponse(val token2: String)

    data class historyObject(val date: Long, val price: Float);
    data class getHistoryEndorsement(val history: ArrayList<historyObject>);
    data class getPriceResponse(val price: Float, val change: String)


    suspend fun handleError(response: HttpResponse): String {
        val responseData = Gson().fromJson(response.bodyAsText(), ErrorResponse::class.java)
        throw IllegalArgumentException(responseData.error)
    }

    suspend fun register(username: String, email: String, password: String, birthday: String): String {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        params.append("username",
            withContext(IO) {
                URLEncoder.encode(username, "UTF-8")
            })
        params.append("email", withContext(IO) {
            URLEncoder.encode(email, "UTF-8")
            })
        params.append("password",
            withContext(IO) {
                URLEncoder.encode(password, "UTF-8")
            })
        params.append("birthday",
            withContext(IO) {
                URLEncoder.encode(birthday, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "register")

        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), RegisterResponse::class.java)
            return responseData.token
        } else {
            handleError(response)
        }
        return "";
    }

    data class LoginResponse(val token: String)

    suspend fun login(username: String, password: String): String {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        params.append("username",
            withContext(IO) {
                URLEncoder.encode(username, "UTF-8")
            })
        params.append("password",
            withContext(IO) {
                URLEncoder.encode(password, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "login")

        val response = client.post(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), LoginResponse::class.java)
            return responseData.token
        } else {
            handleError(response)
        }
        return ""
    }

    suspend fun getPrice(ticker: String): getPriceResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "ticker", "price")
        builder.url.parameters.append("symbol", ticker)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getPriceResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getInfo(ticker: String): GetInfoResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "ticker", "info")
        builder.url.parameters.append("symbol", ticker)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), GetInfoResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getEndorse(ticker: String): getEndorseResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "ticker", "endorsements")
        builder.url.parameters.append("symbol", ticker)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getEndorseResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }


    suspend fun setEndorse(ticker: String): setEndorseResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("symbol",
            withContext(IO) {
                URLEncoder.encode(ticker, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "ticker", "endorsements")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), setEndorseResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getHistory(ticker: String): getHistoryEndorsement? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "ticker", "price", "history")
        builder.url.parameters.append("symbol", ticker)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getHistoryEndorsement::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }




}