package com.example.tiarabakery.model

import okhttp3.Address

data class UserModel(
    val name: String="",
    val email: String="",
    val uid: String="",
    val cartItems : Map<String, Long> = emptyMap(),
    val address: String = ""
)
