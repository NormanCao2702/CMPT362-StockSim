package com.example.cmpt362_stocksim

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Stock")
data class Stock(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name:String = "",
    @ColumnInfo(name = "quantity")
    var quantity:Int = 0,
    @ColumnInfo(name = "total_value")
    var total_value: Double = 0.0,
    @ColumnInfo(name = "cash_value")
    var cash_value: Double = 0.0
)