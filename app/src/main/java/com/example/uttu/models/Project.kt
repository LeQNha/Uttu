package com.example.uttu.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Project(
    val projectId: String,
    var projectName: String,
    var projectStatus: String,
    var createdAt: Date

) : Parcelable
