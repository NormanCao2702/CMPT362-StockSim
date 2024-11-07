package com.example.cmpt362_stocksim

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StockDatabaseRepository(private val stockDatabaseDao: StockDatabaseDao) {

    fun insert(stock: Stock){
        CoroutineScope(IO).launch {
            stockDatabaseDao.insertStock(stock)
        }
    }

    val allStocks: Flow<List<Stock>> = stockDatabaseDao.getAllStocks()

    fun getStockById(id: Long): Stock{return stockDatabaseDao.getStockById(id)}
    fun getStockByName(name: String): Stock{return stockDatabaseDao.getStockByName(name)}

    fun getStockQuantity(id: Long): Int{return stockDatabaseDao.getStockQuantity(id)}
    fun getStockQuantity(name: String): Int{return stockDatabaseDao.getStockQuantity(name)}

    fun getStockTotalValue(id: Long): Double{return stockDatabaseDao.getStockTotalValue(id)}
    fun getStockTotalValue(name: String): Double{return stockDatabaseDao.getStockTotalValue(name)}

    fun getStockCashValue(id: Long): Double{return stockDatabaseDao.getStockCashValue(id)}
    fun getStockCashValue(name: String): Double{return stockDatabaseDao.getStockCashValue(name)}

    fun deleteStock(id: Long){
        CoroutineScope(IO).launch {
            stockDatabaseDao.deleteStock(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(IO).launch {
            stockDatabaseDao.deleteAll()
        }
    }
}