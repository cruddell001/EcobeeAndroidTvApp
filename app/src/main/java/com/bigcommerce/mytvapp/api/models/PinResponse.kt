package com.bigcommerce.mytvapp.api.models

data class PinResponse (
    val ecobeePin: String,
    val code: String,
    val scope: String,
    val expires_in: Int?,
    val interval: Int?
)