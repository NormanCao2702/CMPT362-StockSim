package com.example.cmpt362_stocksim

import androidx.lifecycle.ViewModel
import com.example.cmpt362_stocksim.BackendRepository.GetInfoResponse
import com.example.cmpt362_stocksim.BackendRepository.getEndorseResponse
import com.example.cmpt362_stocksim.BackendRepository.setEndorseResponse
import com.example.cmpt362_stocksim.BackendRepository.getHistoryEndorsement
import com.example.cmpt362_stocksim.BackendRepository.getPriceResponse
import com.example.cmpt362_stocksim.BackendRepository.getCashResponse
import com.example.cmpt362_stocksim.BackendRepository.getBuyResponse
import com.example.cmpt362_stocksim.BackendRepository.getSellResponse
import com.example.cmpt362_stocksim.BackendRepository.getInvResponse
import com.example.cmpt362_stocksim.BackendRepository.setUserAchResponse
import com.example.cmpt362_stocksim.BackendRepository.getUserAchResponse
import com.example.cmpt362_stocksim.BackendRepository.getAllAchResponse
import com.example.cmpt362_stocksim.BackendRepository.friendRequestResponse
import com.example.cmpt362_stocksim.BackendRepository.friendCancelResponse
import com.example.cmpt362_stocksim.BackendRepository.friendAcceptResponse
import com.example.cmpt362_stocksim.BackendRepository.friendDeclineResponse
import com.example.cmpt362_stocksim.BackendRepository.getFriendsResponse
import com.example.cmpt362_stocksim.BackendRepository.getRecievedResponse
import com.example.cmpt362_stocksim.BackendRepository.getSentResponse
import com.example.cmpt362_stocksim.BackendRepository.setRemoveResponse
import com.example.cmpt362_stocksim.BackendRepository.setPostResponse
import com.example.cmpt362_stocksim.BackendRepository.getUserPostResponse
import com.example.cmpt362_stocksim.BackendRepository.getMessageResponse
import com.example.cmpt362_stocksim.BackendRepository.setMessageResponse
import com.example.cmpt362_stocksim.BackendRepository.getCheckResponse
import com.example.cmpt362_stocksim.BackendRepository.getUsersResponse
import com.example.cmpt362_stocksim.BackendRepository.getStockResponse2
import com.example.cmpt362_stocksim.BackendRepository.StockResponseDataClassNews


class BackendViewModel(private val backendRepository: BackendRepository) : ViewModel() {

    suspend fun register(username: String, email: String, password: String, birthday: String): String  {
        return backendRepository.register(username, email, password, birthday)
    }

    suspend fun login(username: String, password: String): String {
        return backendRepository.login(username, password)
    }

    suspend fun getInfo(ticker: String): GetInfoResponse? {
        return backendRepository.getInfo(ticker)
    }
    suspend fun getEndorse(ticker: String): getEndorseResponse? {
        return backendRepository.getEndorse(ticker)
    }

    suspend fun setEndorse(ticker: String, token: String): setEndorseResponse? {
        return backendRepository.setEndorse(ticker, token)
    }

    suspend fun getHistory(ticker: String): getHistoryEndorsement? {
        return backendRepository.getHistory(ticker)
    }

    suspend fun getPrice(ticker: String): getPriceResponse? {
        return backendRepository.getPrice(ticker)
    }

    suspend fun getCash(user: String): getCashResponse? {
        return backendRepository.getCash(user)
    }

    suspend fun buyStock(ticker: String, amount: String, token: String): getBuyResponse? {
        return backendRepository.buyStock(ticker, amount, token)
    }

    suspend fun sellStock(ticker: String, amount: String, token: String): getSellResponse? {
        return backendRepository.sellStock(ticker, amount, token)
    }

    suspend fun getInv(user: String): getInvResponse? {
        return backendRepository.getInv(user)
    }

    suspend fun getUsersAchievement(user: String): getUserAchResponse? {
        return backendRepository.getUsersAchievement(user)
    }

    suspend fun setUsersAchievement(id: String, token: String): setUserAchResponse? {
        return backendRepository.setUsersAchievement(id, token)
    }

    suspend fun getAllAchievements(): getAllAchResponse? {
        return backendRepository.getAllAchievements()
    }

    suspend fun getUserInfo(userId: String): BackendRepository.UserInfoResponse {
        return backendRepository.getUserInfo(userId)
    }

    suspend fun getFeed(): ArrayList<BackendRepository.feedItem> {
        return backendRepository.getFeed()
    }

    suspend fun setFriendRequest(to: String, token: String): friendRequestResponse? {
        return backendRepository.setFriendRequest(to, token)
    }

    suspend fun friendRequestCancel(to: String, token: String): friendCancelResponse? {
        return backendRepository.friendRequestCancel(to, token)
    }

    suspend fun friendRequestAccept(to: String, token: String): friendAcceptResponse? {
        return backendRepository.friendRequestAccept(to, token)
    }

    suspend fun friendRequestDecline(from: String, token: String): friendDeclineResponse? {
        return backendRepository.friendRequestDecline(from, token)
    }

    suspend fun getFriends(user: String): getFriendsResponse? {
        return backendRepository.getFriends(user)
    }

    suspend fun getReceived(token: String): getRecievedResponse? {
        return backendRepository.getReceived(token)
    }

    suspend fun getSent(token: String): getSentResponse? {
        return backendRepository.getSent(token)
    }

    suspend fun setRemove(uid: String, token: String): setRemoveResponse? {
        return backendRepository.setRemove(uid, token)
    }

    suspend fun setPost(content: String, token: String): setPostResponse? {
        return backendRepository.setPost(content, token)
    }

    suspend fun getUserPosts(user: String): getUserPostResponse? {
        return backendRepository.getUserPosts(user)
    }

    suspend fun getMessages(user: String, token: String): getMessageResponse? {
        return backendRepository.getMessages(user, token)
    }

    suspend fun setSendMessage(to: String, content: String, token: String): setMessageResponse? {
        return backendRepository.setSendMessage(to, content, token)
    }

    suspend fun getCheckMessages(user: String, parent: String, token: String): getCheckResponse? {
        return backendRepository.getCheckMessages(user, parent, token)
    }

    suspend fun getUsers(user: String): getUsersResponse? {
        return backendRepository.getUsers(user)
    }

    suspend fun getIsUserFriend(user: String, token: String): Boolean {
        return backendRepository.getIsUserFriend(user, token)
    }

    suspend fun getStocks(sym: String): getStockResponse2? {
        return backendRepository.getStocks(sym)
    }

    suspend fun getNews(sym: String): StockResponseDataClassNews? {
        return backendRepository.getNews(sym)
    }


}