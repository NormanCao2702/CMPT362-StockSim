package com.example.cmpt362_stocksim

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class BlankActivity : AppCompatActivity() {


    private val stock = Stock()
    private lateinit var database: StockDatabase
    private lateinit var databaseDao: StockDatabaseDao
    private lateinit var repository: StockDatabaseRepository
    private lateinit var viewModelFactory: StockDatabaseViewModel.stockViewModelFactory
    private lateinit var stockVViewModel: StockDatabaseViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)
        // Handle custom back button click
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        database = StockDatabase.getInstance(this)
        databaseDao = database.stockDatabaseDao
        repository = StockDatabaseRepository(databaseDao)
        viewModelFactory = StockDatabaseViewModel.stockViewModelFactory(repository)
        stockVViewModel = ViewModelProvider(this, viewModelFactory).get(StockDatabaseViewModel::class.java)


        stockVViewModel.getStockQuantityByName("AAPL") // Trigger the fetch
        stockVViewModel.stockQuantityByName.observe(this) { quantity ->
            println("XD: $quantity") // Access the updated value after LiveData changes
        }

    }

}