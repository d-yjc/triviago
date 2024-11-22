package com.example.triviago.models

data class User(
    val name: String,
    val score: Int,
    val questionWins: Int,
    val questionLosses: Int)
