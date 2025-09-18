package com.example.uttu.models

import java.util.Date

data class FriendRequested(
    val requestId: String,
    val requestSenderId: String,
    val requestReceiverId: String,
    var createdAt: Date = Date()
)
