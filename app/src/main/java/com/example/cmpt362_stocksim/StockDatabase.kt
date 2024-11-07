package com.example.cmpt362_stocksim

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Stock::class], version = 1)
abstract class StockDatabase: RoomDatabase() {
    abstract val stockDatabaseDao: StockDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getInstance(context: Context): StockDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        StockDatabase::class.java, "StockSim DB").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}