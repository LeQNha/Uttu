package com.example.uttu.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    val projectId: String,
    var projectName: String,
    var projectDescription: String,
    var projectStatus: String

) : Parcelable
