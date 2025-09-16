package com.example.uttu.repositories

import android.util.Log
import com.example.uttu.firebase.FirebaseInstance
import com.example.uttu.models.Leader
import com.example.uttu.models.Member
import com.example.uttu.models.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class ProjectRepository {

    private val auth: FirebaseAuth = FirebaseInstance.auth
    private val firestore: FirebaseFirestore = FirebaseInstance.firebaseFirestoreInstance

    suspend fun createProject(projectName: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("User not logged in"))

            // 1. Tạo document cho Project
            val projectId = firestore.collection("projects").document().id
            val project = Project(
                projectId = projectId,
                projectName = projectName,
                projectStatus = "Planning",
                createdAt = Date()
            )

            firestore.collection("projects").document(projectId).set(project).await()

            // 2. Tạo document trong leaders
            val leader = Leader(
                leaderId = currentUser.uid,
                teamId = projectId
            )
            firestore.collection("leaders").add(leader).await()

            // 3. Tạo document trong members
            val member = Member(
                memberId = currentUser.uid,
                teamId = projectId
            )
            firestore.collection("members").add(member).await()

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProjects():Result<List<Project>>{
        return try{
            val currentUser = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
            Log.d("DEBUG", "Current User UID: ${currentUser.uid}")

            val memberSnapshot = firestore.collection("members")
                .whereEqualTo("memberId", currentUser.uid)
                .get()
                .await()
            Log.d("DEBUG", "Members size: ${memberSnapshot.size()}")

            val projectIds = memberSnapshot.documents.mapNotNull { it.getString("teamId") }
            Log.d("DEBUG", "Project IDs: $projectIds")

            if (projectIds.isEmpty()) {
                return Result.success(emptyList())
            }

            val projectsSnapshot = firestore.collection("projects")
                .whereIn("projectId", projectIds)
                .get()
                .await()
            Log.d("DEBUG", "Projects size: ${projectsSnapshot.size()}")

            val projects = projectsSnapshot.toObjects(Project::class.java)
            Log.d("DEBUG", "Projects: $projects")

            Result.success(projects)
        }catch (e: Exception) {
            Log.e("DEBUG", "Error in getUserProjects", e)
            Result.failure(e)
        }
    }
}