package com.example.cmpt362_stocksim


data class StockResultDataClassNews (    val id: String,
                                         val publisher: Publish,
                                         val title: String,
                                         val author: String,
                                         val published_utc: String,
                                         val article_url: Double,
                                         val image_url: String,
                                         val description: String)


data class Publish(
    val name: String,
    val homepage_url: String,
)