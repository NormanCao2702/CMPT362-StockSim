package com.example.cmpt362_stocksim

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch


/*
THIS ACTIVITY IS THE STOCK INVENTORY CHANGE NAME LATER..
 */

class StockInventory : AppCompatActivity() {

    private lateinit var container: LinearLayout

    val repository2 = BackendRepository()
    val viewModelFactory2 = BackendViewModelFactory(repository2)
    val backendViewModel = viewModelFactory2.create(BackendViewModel::class.java)

    private val userDataManager by lazy { UserDataManager(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stockinventory)

        container = findViewById(R.id.stockContainer)

        // NEED TO GET USER ID HERE ASWELL
        // DONT FORGETTTTTTT
        lifecycleScope.launch {
            try {
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getInv(it) }
                if (response != null) {
                    for (stock in response.stocks) {

                        try {
                            val response2 = backendViewModel.getPrice(stock.symbol)
                            if (response2 != null) {
                                val temp = response2.price

                                val valueOfStock = (temp * stock.amount.toFloat())
                                val formattedValue = String.format("%.2f", valueOfStock)

                                val horizontalLayout = LinearLayout(this@StockInventory).apply {
                                    orientation = LinearLayout.HORIZONTAL
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                }

                                val leftTextView = TextView(this@StockInventory).apply {
                                    text = "${stock.symbol}:    ${stock.amount}"
                                    textSize = 25f
                                    setTextColor(Color.BLACK)
                                    setPadding(8, 16, 8, 16)
                                    layoutParams = LinearLayout.LayoutParams(
                                            0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1f // Weight for left alignment
                                        )
                                    }

                                val rightTextView = TextView(this@StockInventory).apply {
                                    text = "Value: $$formattedValue"
                                    textSize = 25f
                                    setTextColor(Color.BLACK)
                                    setPadding(8, 16, 8, 16)
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                }

                                horizontalLayout.addView(leftTextView)
                                horizontalLayout.addView(rightTextView)
                                container.addView(horizontalLayout)

                            }
                        } catch (e: IllegalArgumentException) {
                            Log.d("MJR", e.message!!)
                        }

                        val divider = View(this@StockInventory).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, // Width
                                6 // Height (thickness of the line)
                            ).apply {
                                setMargins(12, 16, 12, 16) // Optional margins
                            }
                            setBackgroundColor(Color.LTGRAY) // Line color
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