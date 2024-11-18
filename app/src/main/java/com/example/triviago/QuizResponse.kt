package com.example.triviago

data class QuizResponse(
    var date: Long = 0L,
    var category: String = "",
    var totalScore: Int = 0,
    var isBoolean: Boolean = false // Updated type from Boolean to String for clarity
)
