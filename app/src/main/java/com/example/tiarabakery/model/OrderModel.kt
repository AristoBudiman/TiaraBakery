package com.example.tiarabakery.model

import com.google.firebase.Timestamp

data class OrderModel(
    val id: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val userId: String = "",
    val userEmail: String = "",
    val phone: String = "",
    val address: String = "",
    val items: Map<String, Long> = emptyMap(),
    val status: String = "",
    val total: Long = 0
)

