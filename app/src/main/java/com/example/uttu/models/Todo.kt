package com.example.uttu.models

import java.util.Date

data class Todo(
    val todoId: String,
    val taskId: String,           // Mỗi todo thuộc về 1 task
    var todoTitle: String,
    var todoDescription: String,
    var todoStatus: Boolean,      // true = Done, false = Pending
    val createdAt: Date
)
