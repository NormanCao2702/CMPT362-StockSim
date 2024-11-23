package com.example.cmpt362_stocksim.api

import com.example.cmpt362_stocksim.StockResponsDataClass
import com.google.gson.Gson
import com.google.gson.JsonArray
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArrayBuilder

class BackendRepository {
    private val HOST = "stocksim.breadmod.info"
    private val API_PATH = "api"
    private val client = HttpClient(CIO)

    @Serializable
    data class ErrorResponse(val error: String)

    @Serializable
    data class RegisterResponse(val JWT: String)

    suspend fun handleError(response: HttpResponse): String {
        val responseData = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
        throw IllegalArgumentException(responseData.error)
    }

    suspend fun register(username: String, email: String, password: String, birthday: String): String {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        params.append("username", username)
        params.append("email", email)
        params.append("password", password)
        params.append("birthday", birthday)
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "register")

        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Json.decodeFromString<RegisterResponse>(response.bodyAsText())
            return responseData.JWT
        } else {
            handleError(response)
        }
        return "";
    }
}