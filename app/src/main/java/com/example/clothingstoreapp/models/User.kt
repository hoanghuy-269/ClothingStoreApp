package com.example.clothingstoreapp.models

data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val gender: String ?= null,
    val avatarURI: String? = null,
    val address:String = "",
    val role:String = "user",
)
