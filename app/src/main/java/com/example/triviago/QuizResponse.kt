package com.example.triviago

import com.google.firebase.firestore.PropertyName

data class QuizResponse(
    var category: String = "",
    var date: Long = 0L,
    var score: Int = 0,
    var numQuestions : Int = 0,
    @PropertyName("type") var type: String = ""
)
