package com.example.uttu.repositories

import android.util.Log
import com.example.uttu.firebase.FirebaseInstance
import com.example.uttu.models.Leader
import com.example.uttu.models.Member
import com.example.uttu.models.Project
import com.example.uttu.models.TeamMember
import com.example.uttu.models.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class ProjectRepository {

    private val auth: FirebaseAuth = FirebaseInstance.auth
    private val firestore: FirebaseFirestore = FirebaseInstance.firebaseFirestoreInstance

    fun getCurrentUserId(): String? = auth.currentUser?.uid

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

    suspend fun addMemberToTeam(teamId: String, userId: String): Result<Boolean>{
        return try {
            val membersRef = firestore.collection("members")

            // kiểm tra đã tồn tại chưa
            val querySnapshot = membersRef
                .whereEqualTo("memberId", userId)
                .whereEqualTo("teamId", teamId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                // đã tồn tại
                Result.failure(Exception("Member already in the team"))
            } else {
                val memberData = mapOf(
                    "memberId" to userId,
                    "teamId" to teamId
                )
                membersRef.add(memberData).await()
                Result.success(true)
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loadTeamMembers(teamId: String, onResult: (Result<List<TeamMember>>) -> Unit){
        val teamMembers = mutableListOf<TeamMember>()

        firestore.collection("leaders")
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener { leaderSnapshot ->
                val leaderIds = leaderSnapshot.mapNotNull { it.getString("leaderId") }.toSet()

                firestore.collection("members")
                    .whereEqualTo("teamId", teamId)
                    .get()
                    .addOnSuccessListener { memberSnapshot ->
                        val memberTasks = memberSnapshot.map { doc ->
                            val memberId = doc.getString("memberId") ?: return@map null
                            firestore.collection("users").document(memberId).get()
                                .continueWith { task ->
                                    task.result?.toObject(User::class.java)?.let {user ->
                                        val role = if (leaderIds.contains(memberId)) "Leader" else "Member"
                                        teamMembers.add(TeamMember(user, role))
                                    }
                                }
                        }.filterNotNull()

                        Tasks.whenAllSuccess<Void>(memberTasks)
                            .addOnSuccessListener {
                                onResult(Result.success(teamMembers))
                            }
                            .addOnFailureListener { e -> onResult(Result.failure(e)) }
                    }
                    .addOnFailureListener { e -> onResult(Result.failure(e)) }

            }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }

    suspend fun removeMemberFromTeam(teamId: String, memberId: String): Result<Boolean>{
        return try {
            val membersRef = firestore.collection("members")
            val leadersRef = firestore.collection("leaders")

            val memberQuery = membersRef
                .whereEqualTo("teamId", teamId)
                .whereEqualTo("memberId", memberId)
                .get()
                .await()

//            if (querySnapshot.isEmpty) {
//                Result.failure(Exception("Member not found in team"))
//            } else {
//                for (doc in querySnapshot.documents) {
//                    doc.reference.delete().await()
//                }
//
//                Result.success(true)
//            }
            for (doc in memberQuery.documents) {
                doc.reference.delete().await()
            }

            // Xóa trong leaders nếu có
            val leaderQuery = leadersRef
                .whereEqualTo("teamId", teamId)
                .whereEqualTo("leaderId", memberId)
                .get()
                .await()

            for (doc in leaderQuery.documents) {
                doc.reference.delete().await()
            }

            Result.success(true)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProject(teamId: String): Result<Boolean>{
        return try {
            val batch = firestore.batch()

            //xóa project
            val projectRef = firestore.collection("projects").document(teamId)
            batch.delete(projectRef)

            val membersSnapshot = firestore.collection("members")
                .whereEqualTo("teamId", teamId)
                .get()
                .await()
            for (doc in membersSnapshot.documents){
                batch.delete(doc.reference)
            }

            // Xóa leaders
            val leadersSnapshot = firestore.collection("leaders")
                .whereEqualTo("teamId", teamId)
                .get()
                .await()
            for (doc in leadersSnapshot.documents) {
                batch.delete(doc.reference)
            }

            batch.commit().await()

            Result.success(true)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateProjectName( projectId: String,
                           newName: String,
                           onResult: (Result<Unit>) -> Unit){
        firestore.collection("projects")
            .document(projectId)
            .update("projectName", newName)
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                onResult(Result.failure(e))
            }
    }

    // Update project status
    fun updateProjectStatus(projectId: String, newStatus: String, callback: (Result<Unit>) -> Unit) {
        firestore.collection("projects").document(projectId)
            .update("projectStatus", newStatus)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e -> callback(Result.failure(e)) }
    }
}