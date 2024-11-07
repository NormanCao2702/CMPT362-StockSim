package com.example.cmpt362_stocksim


data class StockResultDataClassNews (    val id: String,
                                         val publisher: Publisher,
                                         val title: String,
                                         val author: String,
                                         val published_utc: String,
                                         val article_url: String,
                                         val image_url: String,
                                         val description: String)


data class Publisher(
    val name: String,
    val homepage_url: String
)