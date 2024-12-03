package com.example.cmpt362_stocksim.ui.inventory

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

/**
 * This class is the stock inventory in homepage
 */
class StockInventory : AppCompatActivity() {

    // Setup stock container and user data manager
    private lateinit var container: LinearLayout
    private val userDataManager by lazy { UserDataManager(this) }

    // Setup backend repository
    val repository2 = BackendRepository()
    val viewModelFactory2 = BackendViewModelFactory(repository2)
    val backendViewModel = viewModelFactory2.create(BackendViewModel::class.java)

    // This function gets the users favourite stocks
    private suspend fun getFavorites(): List<String> {
        val userId = userDataManager.getUserId()
        return userId?.let { backendViewModel.getFavorites(it) } ?: emptyList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stockinventory)

        container = findViewById(R.id.stockContainer)

        lifecycleScope.launch {
            try {
                val userId = userDataManager.getUserId()
                val favorites = getFavorites()  // Get current favorites
                val response = userId?.let { backendViewModel.getInv(it) }

                if (response != null) {
                    // For each stock that the user has
                    for (stock in response.stocks) {
                        try {
                            // get that stocks price and calculate total amount worth
                            val response2 = backendViewModel.getPrice(stock.symbol)
                            if (response2 != null) {
                                val temp = response2.price
                                val valueOfStock = (temp * stock.amount.toFloat())
                                val formattedValue = String.format("%.2f", valueOfStock)

                                // Main horizontal layout
                                val horizontalLayout = LinearLayout(this@StockInventory).apply {
                                    orientation = LinearLayout.HORIZONTAL
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    gravity = Gravity.CENTER_VERTICAL
                                }

                                // Left side layout (symbol and amount)
                                val leftTextView = TextView(this@StockInventory).apply {
                                    text = "${stock.symbol}:    ${stock.amount}"
                                    textSize = 25f
                                    setTextColor(Color.BLACK)
                                    setPadding(8, 16, 8, 16)
                                    layoutParams = LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1f
                                    )
                                }

                                // Center value text
                                val centerTextView = TextView(this@StockInventory).apply {
                                    text = "Value: $$formattedValue"
                                    textSize = 25f
                                    setTextColor(Color.BLACK)
                                    setPadding(8, 16, 8, 16)
                                }

                                // Favorite button
                                val favoriteButton = ImageButton(this@StockInventory).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        marginStart = 16
                                    }
                                    background = null
                                    setImageResource(
                                        if (favorites.contains(stock.symbol))
                                            R.drawable.ic_star_filled
                                        else R.drawable.ic_star_outline
                                    )

                                    setOnClickListener {
                                        lifecycleScope.launch {
                                            try {
                                                val token = userDataManager.getJwtToken()
                                                if (token != null) {
                                                    if (favorites.contains(stock.symbol)) {
                                                        // Remove from favorites
                                                        val success = backendViewModel.removeFavorite(stock.symbol, token)
                                                        if (success) {
                                                            setImageResource(R.drawable.ic_star_outline)
                                                            Toast.makeText(this@StockInventory,
                                                                "Removed from favorites",
                                                                Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        // Add to favorites
                                                        val success = backendViewModel.addFavorite(stock.symbol, token)
                                                        if (success) {
                                                            setImageResource(R.drawable.ic_star_filled)
                                                            Toast.makeText(this@StockInventory,
                                                                "Added to favorites",
                                                                Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(this@StockInventory,
                                                    "Failed to update favorites",
                                                    Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }

                                horizontalLayout.addView(leftTextView)
                                horizontalLayout.addView(centerTextView)
                                horizontalLayout.addView(favoriteButton)
                                container.addView(horizontalLayout)
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.d("MJR", e.message!!)
                        }

                        // Add divider
                        val divider = View(this@StockInventory).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                6
                            ).apply {
                                setMargins(12, 16, 12, 16)
                            }
                            setBackgroundColor(Color.LTGRAY)
                        }
                        container.addView(divider)
                    }
                }
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

}