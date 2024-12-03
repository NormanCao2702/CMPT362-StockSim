package com.example.cmpt362_stocksim.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

    data class achieves(val id: Int, val date: Long, val name: String, val description: String)
    data class getUserAchResponse(val achievements: ArrayList<achieves>)
    data class setUserAchResponse(val token5: String)
    data class allAchieves(val name: String, val description: String, val id: String)
    data class getAllAchResponse(val achievements: ArrayList<allAchieves>)

    data class feedItem(val content: String, val date: Long, val post_id: Int, val uid: Int, val username: String)
    data class feed(val feed: ArrayList<feedItem>)

    data class friendRequestResponse(val token6: String)
    data class friendCancelResponse(val token7: String)
    data class friendAcceptResponse(val token8: String)
    data class friendDeclineResponse(val token8: String)

    data class friend(val uid: Int, val username: String, val added_date: String)
    data class getFriendsResponse(val friends: ArrayList<friend>)

    data class request(val uid: Int, val username: String, val sent_date: Long)
    data class getRecievedResponse(val requests: ArrayList<request>)
    data class getSentResponse(val requests: ArrayList<request>)

    data class setRemoveResponse(val token9: String)
    data class setPostResponse(val token10: String)


    data class post(val content: String, val date: Long, val post_id: Int)
    data class getUserPostResponse(val posts: ArrayList<post>)

    data class message(val content: String, val date: Long, val from: Int, val username: String, val message_id: Int)
    data class getMessageResponse(val messages: ArrayList<message>)

    data class setMessageResponse(val token11: String)

    data class getCheckResponse(val messages: ArrayList<message>)

    data class user(val uid: Int, val username: String)

    data class getUsersResponse(val users: ArrayList<user>)

    data class isFriendResponse(val is_friend: Boolean)

    data class stock(val symbol: String, val name: String, val price: Float)
    data class getStockResponse2(val tickers: ArrayList<stock>)


    data class StockResponseDataClassNews (val results: List<StockResultDataClassNews>)

    data class StockResultDataClassNews (val id: String,
                                         val publisher: Publisher,
                                         val title: String,
                                         val author: String,
                                         val published_utc: String,
                                         val article_url: String,
                                         val image_url: String,
                                         val description: String)

    data class Publisher(
        val name: String,
        val homepage_url: String
    )



    suspend fun handleError(response: HttpResponse): String {
        var errorMessage = "An error connecting to the backend has occurred! This may be caused by using SFU Wifi."
        try {
            errorMessage = Gson().fromJson(response.bodyAsText(), ErrorResponse::class.java).error
        } catch(e: JsonSyntaxException) {

        }

        throw IllegalArgumentException(errorMessage)
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

    suspend fun getNews(sym: String): StockResponseDataClassNews? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "ticker", "news")
        builder.url.parameters.append("symbol", sym)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), StockResponseDataClassNews::class.java)
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

    suspend fun getFeed(): ArrayList<feedItem> {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "feed")
        builder.url.parameters.append("limit", "1000")

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            return Gson().fromJson(response.bodyAsText(), feed::class.java).feed
        } else {
            handleError(response)
        }
        throw IllegalStateException("Failed to get favorites")
    }

    suspend fun setFriendRequest(to: String, token: String): friendRequestResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("to",
            withContext(IO) {
                URLEncoder.encode(to, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friend_request")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), friendRequestResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun friendRequestCancel(to: String, token: String): friendCancelResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("symbol",
            withContext(IO) {
                URLEncoder.encode(to, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friend_request", "cancel")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), friendCancelResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun friendRequestAccept(to: String, token: String): friendAcceptResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("from",
            withContext(IO) {
                URLEncoder.encode(to, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friend_request", "accept")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), friendAcceptResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun friendRequestDecline(from: String, token: String): friendDeclineResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("from",
            withContext(IO) {
                URLEncoder.encode(from, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friend_request", "decline")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), friendDeclineResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }


    suspend fun getFriends(user: String): getFriendsResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friends")
        builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getFriendsResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getReceived(token: String): getRecievedResponse? {
        val builder = HttpRequestBuilder()
        builder.header("Authorization", "Bearer " + token)
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friend_requests", "received")
        //builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getRecievedResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getSent(token: String): getSentResponse? {
        val builder = HttpRequestBuilder()
        builder.header("Authorization", "Bearer " + token)
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friend_request", "sent")
        //builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getSentResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun setRemove(uid: String, token: String): setRemoveResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("uid",
            withContext(IO) {
                URLEncoder.encode(uid, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "friends", "remove")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), setRemoveResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }



    suspend fun setPost(content: String, token: String): setPostResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("content",
            withContext(IO) {
                URLEncoder.encode(content, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "post")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), setPostResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getUserPosts(user: String): getUserPostResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "posts")
        builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getUserPostResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }



    suspend fun getMessages(user: String, token: String): getMessageResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "messages")
        builder.url.parameters.append("uid", user)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getMessageResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }




    suspend fun setSendMessage(to: String, content: String, token: String): setMessageResponse? {
        val builder = HttpRequestBuilder()
        val params = ParametersBuilder(0)
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        params.append("to",
            withContext(IO) {
                URLEncoder.encode(to, "UTF-8")
            })
        params.append("content",
            withContext(IO) {
                URLEncoder.encode(content, "UTF-8")
            })
        builder.url.encodedParameters = params
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "messages")
        val response = client.post(builder)
        if(response.status == HttpStatusCode.Created) {
            val responseData = Gson().fromJson(response.bodyAsText(), setMessageResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }




    suspend fun getCheckMessages(user: String, parent: String, token: String): getCheckResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.header("Authorization", "Bearer " + token)
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "get_messages_after")
        builder.url.parameters.append("uid", user)
        builder.url.parameters.append("parent", parent)


        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getCheckResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }

    suspend fun getUsers(user: String): getUsersResponse? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "user", "search")
        builder.url.parameters.append("username", user)


        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getUsersResponse::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }



    suspend fun getStocks(sym: String): getStockResponse2? {
        val builder = HttpRequestBuilder()
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "ticker", "search")
        builder.url.parameters.append("symbol", sym)

        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), getStockResponse2::class.java)
            return responseData
        } else {
            handleError(response)
        }
        return null
    }



    suspend fun getIsUserFriend(user: String, token: String): Boolean {
        val builder = HttpRequestBuilder()
        builder.header("Authorization", "Bearer " + token)
        builder.url.protocol = URLProtocol.HTTPS
        builder.url.host = HOST
        builder.url.path(API_PATH, "social", "is_friend")
        builder.url.parameters.append("uid", user)
        val response = client.get(builder)
        if(response.status == HttpStatusCode.OK) {
            val responseData = Gson().fromJson(response.bodyAsText(), isFriendResponse::class.java)
            return responseData.is_friend
        } else {
            handleError(response)
        }
        return false
    }


}