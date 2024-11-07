package com.example.cmpt362_stocksim

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDatabaseDao {

    @Insert
    suspend fun insertStock(stock: Stock)



    @Query("SELECT * FROM Stock")
    fun getAllStocks(): Flow<List<Stock>>

    @Query("SELECT * FROM Stock WHERE name = :name")
    fun getStockByName(name: String): Stock

    @Query("SELECT quantity FROM Stock WHERE name = :name")
    fun getStockQuantity(name: String): Int

    @Query("SELECT total_value FROM Stock WHERE name = :name")
    fun getStockTotalValue(name: String): Double

    @Query("SELECT cash_value FROM Stock WHERE name = :name")
    fun getStockCashValue(name: String): Double

    @Query("SELECT * FROM Stock WHERE id = :key")
    fun getStockById(key: Long): Stock

    @Query("SELECT quantity FROM Stock WHERE id = :key")
    fun getStockQuantity(key: Long): Int

    @Query("SELECT total_value FROM Stock WHERE id = :key")
    fun getStockTotalValue(key: Long): Double

    @Query("SELECT cash_value FROM Stock WHERE id = :key")
    fun getStockCashValue(key: Long): Double


    @Update
    suspend fun updateStock(stock: Stock)

    @Query("UPDATE Stock SET quantity = :newQuantity WHERE name = :name")
    suspend fun setStockQuantity(name: String, newQuantity: Int)

    @Query("UPDATE Stock SET total_value = :newTotalValue WHERE name = :name")
    suspend fun setStockTotalValue(name: String, newTotalValue: Double)

    @Query("UPDATE Stock SET cash_value = :newCashValue WHERE name = :name")
    suspend fun setStockCashValue(name: String, newCashValue: Double)


    @Query("DELETE FROM Stock")
    suspend fun deleteAll()

    @Query("DELETE FROM Stock WHERE id = :key")
    suspend fun deleteStock(key: Long)
}