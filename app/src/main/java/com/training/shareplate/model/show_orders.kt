package com.training.shareplate.model

data class OrderItem(
    var orderId : String = "",
    val orderedBy : String = "",
    val requestDate : String = ""
)