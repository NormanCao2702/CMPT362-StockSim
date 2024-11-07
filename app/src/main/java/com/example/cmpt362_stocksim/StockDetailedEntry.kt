package com.example.cmpt362_stocksim

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StockDetailedEntry : AppCompatActivity() {

    private lateinit var bckButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_detailed_entry)

        bckButton = findViewById(R.id.entryBackButton)

        val getData = intent.getParcelableExtra<StockSearchDataClass>("android")
        if (getData != null){
            val detailTitle: TextView = findViewById(R.id.detailedTitle)
            val detailDesc: TextView = findViewById(R.id.detailedDescription)
            val detailImage: ImageView = findViewById(R.id.detailImage)

            detailTitle.text = getData.ticker
            detailDesc.text = getData.dataDesc
            detailImage.setImageResource(getData.dataDetailImage)
        }

        bckButton.setOnClickListener{
            this.finish()
        }
    }
}