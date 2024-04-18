package com.example.scarletexample.sockets.modules

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ticker(
    @Json(name = "product_id") val productId: String,
    val price: String,
)
