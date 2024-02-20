package com.training.shareplate.model

data class ShareItem(
    val reason : String = "",
    val time : String = "",
    val peopleCount : String = "",
    val location : String = "",
    var docId : String = ""
)