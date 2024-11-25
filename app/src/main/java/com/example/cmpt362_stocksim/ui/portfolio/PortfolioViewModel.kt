package com.example.cmpt362_stocksim.ui.portfolio

import android.util.Log
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
            val achievements = repository.getUsersAchievement(userId)
            val achievementCount = achievements?.achievements?.size ?: 0
            Log.d("PortfolioVM", "Achievements: ${achievementCount}")

            val stocks = repository.getInv(userId)
            val stocksCount = stocks?.stocks?.size ?: 0
            Log.d("PortfolioVM", "Stocks: ${stocksCount}")

            val favorites = repository.getUserFavorites(userId)
            Log.d("PortfolioVM", "Favorites: ${favorites.favorites.size}")

            // Update LiveData with counts
            val newStats = StatsData(
                achievementCount = achievementCount,
                stocksCount = stocksCount,
                favoritesCount = favorites.favorites.size
            )
            Log.d("PortfolioVM", "Setting new stats: $newStats")
            _statsData.postValue(newStats)

        } catch (e: Exception) {
            Log.e("PortfolioVM", "Error loading stats", e)
            e.printStackTrace()
        }
    }
}