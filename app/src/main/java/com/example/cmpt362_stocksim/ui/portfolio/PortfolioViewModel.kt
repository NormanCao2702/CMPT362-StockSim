package com.example.cmpt362_stocksim.ui.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cmpt362_stocksim.BackendRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PortfolioViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _isLoading.value = false
    }

    private val _statsData = MutableLiveData<StatsData>()
    val statsData: LiveData<StatsData> = _statsData

    data class StatsData(
        val achievementCount: Int = 0,
        val stocksCount: Int = 0,
        val favoritesCount: Int = 0
    )

    suspend fun loadUserStats(userId: String, repository: BackendRepository) {
        try {
            // Launch coroutines in a structured scope
            coroutineScope {
                // Fetch all data in parallel
                val achievementsDeferred = async { repository.getUserAchievements(userId) }
                val stocksDeferred = async { repository.getUserStocks(userId) }
                val favoritesDeferred = async { repository.getUserFavorites(userId) }

                // Await results from deferred objects
                val achievements = achievementsDeferred.await()
                val stocks = stocksDeferred.await()
                val favorites = favoritesDeferred.await()

                // Update LiveData with counts
                _statsData.value = StatsData(
                    achievementCount = achievements.achievements.size,
                    stocksCount = stocks.stocks.size,
                    favoritesCount = favorites.favorites.size
                )
            }
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
    }
}