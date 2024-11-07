package com.example.cmpt362_stocksim


data class StockResponseDataClass(
    val results: StockResultDataClassInfo // This is now an object, not a list
)


data class StockResultDataClassInfo (    val address: Address,
                                         val currency_name: String,
                                         val description: String,
                                         val homepage_url: String,
                                         val list_date: String,
                                         val market_cap: Double,
                                         val name: String,
                                         val phone_number: String,
                                         val total_employees: Int)

data class Address(
    val address1: String,
    val city: String,
    val state: String
)