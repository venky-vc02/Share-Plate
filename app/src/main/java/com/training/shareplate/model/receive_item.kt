package com.training.shareplate.model

data class ReceiveItem(
    val reason : String = "",
    val listedBy : String = "",
    val time : String = "",
    val peopleCount : String = "",
    val location : String = "",
    var docId : String = "",
    val status : String = ""
)