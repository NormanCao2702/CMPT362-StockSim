package com.example.cmpt362_stocksim

import android.content.Context
import android.util.Log
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.headers
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

   // private val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzIxMTI5OTgsImV4cCI6MTczNzI5Njk5OCwidWlkIjoxNSwidXNlcm5hbWUiOiJhZG1pbiJ9.RNaWkEsx0UlrgbxqZirG1xXz7CfMa5hhXyjCLNmPGHo"

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
    data class getHistoryEndorsement(val history: ArrayList<historyObject>)
    data class getPriceResponse(val price: Float, val change: String)
    data class getCashResponse(val cash: Float)
    data class getBuyResponse(val token3: String)
    data class getSellResponse(val token4: String)

    data class stockInv(val symbol: String, val amount: String)
    data class getInvResponse(val stocks: ArrayList<stockInv>)

    data class achieves(val id: Int, val date: Long)
    data class getUserAchResponse(val achievements: ArrayList<achieves>)
    data class setUserAchResponse(val token5: String)
    data class allAchieves(val name: String, val description: String, val id: String)
    data class getAllAchResponse(val achievements: ArrayList<allAchieves>)

    suspend fun handleError(response: HttpResponse): String {
        val responseData = Gson().fromJson(response.bodyAsText(), ErrorResponse::class.java)
        throw IllegalArgumentException(responseData.error)
    }

    suspend fun setUsersAchievement(id: String, token: String): setUserAchResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("id",
            withContext(IO) {
                URLEncoder.encode(id, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "achievement")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), setUserAchResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }


    suspend fun getAllAchievements(): getAllAchResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "achievement")

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getAllAchResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getUsersAchievement(user: String): getUserAchResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "achievement")
        builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getUserAchResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun buyStock(ticker: String, amount: String, token: String): getBuyResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("symbol",
            withContext(IO) {
                URLEncoder.encode(ticker, "UTF-8")
            })
        params.append("amount",
            withContext(IO) {
                URLEncoder.encode(amount, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "stocks", "buy")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), getBuyResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun sellStock(ticker: String, amount: String, token: String): getSellResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("symbol",
            withContext(IO) {
                URLEncoder.encode(ticker, "UTF-8")
            })
        params.append("amount",
            withContext(IO) {
                URLEncoder.encode(amount, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "stocks", "sell")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), getSellResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
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

    data class UserInfoResponse(
        val username: String,
        val email: String,
        val birthday: String,
        val net_worth: Double
    )

    data class NetWorthResponse(
        val net_worth: Double
    )

    suspend fun getUserInfo(userId: String): UserInfoResponse {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "info")
        builder.url.parameters.append("uid", userId)

        // Get basic user info
        val infoResponse = client.get(builder)
        if(infoResponse.status != HttpStatusCode.OK) {
            handleError(infoResponse)
        }

        // Get net worth
        builder.url.path(API_PATH, "user", "net_worth")
        val netWorthResponse = client.get(builder)
        if(netWorthResponse.status != HttpStatusCode.OK) {
            handleError(netWorthResponse)
        }

        val userInfo = Gson().fromJson(infoResponse.bodyAsText(), UserInfoResponse::class.java)
        val netWorth = Gson().fromJson(netWorthResponse.bodyAsText(), NetWorthResponse::class.java)

        return UserInfoResponse(
            username = userInfo.username,
            email = userInfo.email,
            birthday = userInfo.birthday,
            net_worth = netWorth.net_worth
        )
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

    suspend fun getInv(user: String): getInvResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "stocks")
        builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getInvResponse::class.java)
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

    suspend fun getCash(user: String): getCashResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "cash")
        builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getCashResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun setEndorse(ticker: String, token: String): setEndorseResponse? {
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

    data class UserFavorites(
        val favorites: List<String>  // List of stock symbols
    )

    suspend fun getUserFavorites(userId: String): UserFavorites {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "favorites")
        builder.url.parameters.append("uid", userId)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            return Gson().fromJson(response.bodyAsText(), UserFavorites::class.java)
        } else {
            handleError(response)
        }
        throw IllegalStateException("Failed to get favorites")
    }
}