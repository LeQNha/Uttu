package com.example.uttu.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.Date

@Parcelize
data class Task(
    val taskId: String,
    var taskTitle: String,
    var taskDescription: String,
    var taskStatus: String,
    var taskDue: Date,
    val projectId: String
): Parcelable
