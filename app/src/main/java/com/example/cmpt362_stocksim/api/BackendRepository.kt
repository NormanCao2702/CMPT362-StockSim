package com.example.cmpt362_stocksim.api

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
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

    data class ErrorResponse(val error: String)

    data class RegisterResponse(val token: String)

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
}