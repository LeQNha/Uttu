package com.example.uttu.models

data class User(
    val userId: String,
    var username: String,
    var email: String,
    var phoneNumber: String = "",
    var homeAddress: String = ""
)
