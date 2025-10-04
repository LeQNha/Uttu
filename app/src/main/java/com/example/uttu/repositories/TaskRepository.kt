package com.example.uttu.repositories

import android.util.Log
import com.example.uttu.firebase.FirebaseInstance
import com.example.uttu.models.Task
import com.example.uttu.models.User
import com.example.uttu.models.assignedMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository {

    private val auth: FirebaseAuth = FirebaseInstance.auth
    private val firestore: FirebaseFirestore = FirebaseInstance.firebaseFirestoreInstance

    private val tasksCollection = firestore.collection("tasks")
    private val assignedMembersCollection = firestore.collection("assignedMembers")

    fun addTask(task: Task, onResult: (Boolean, String?) -> Unit){
        val docRef = tasksCollection.document()
        val taskWithId = task.copy(taskId = docRef.id)

        docRef.set(taskWithId)
            .addOnSuccessListener {
                onResult(true, docRef.id)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun getTasksByProjectId(projectId: String, onResult: (List<Task>) -> Unit) {
        tasksCollection
            .whereEqualTo("projectId", projectId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("TaskRepository", "Query success: found ${snapshot.size()} docs for projectId=$projectId")
                snapshot.documents.forEach { doc ->
                    Log.d("TaskRepository", "Doc: ${doc.id}, data=${doc.data}")
                }

                val tasks = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Task::class.java)
                }
                onResult(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("TaskRepository", "Query failed: ${e.message}", e)
                onResult(emptyList())
            }
    }

    fun assignMemberToTask(taskId: String, memberId: String, onResult: (Boolean, String?) -> Unit){
        assignedMembersCollection
            .whereEqualTo("taskId", taskId)
            .whereEqualTo("memberId", memberId)
            .get()
            .addOnSuccessListener { snapshot ->
                if(!snapshot.isEmpty){
                    onResult(false, "Member already assigned")
                }else{
                    val docRef = assignedMembersCollection.document()
                    val assigned = assignedMember(assignedMemberId = docRef.id, taskId = taskId, memberId = memberId)
                    docRef.set(assigned)
                        .addOnSuccessListener {
                            assignedMembersCollection.whereEqualTo("taskId", taskId).get()
                                .addOnSuccessListener { membersSnap ->
                                    if(membersSnap.size() == 1){
                                        tasksCollection.document(taskId).update("taskStatus", "Assigned")
                                    }
                                    onResult(true, null)
                                }
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message)
                        }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun getAssignedMembersByTaskId(taskId: String, onResult: (List<User>) -> Unit){
        assignedMembersCollection
            .whereEqualTo("taskId", taskId)
            .get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.isEmpty){
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                val memberIds = snapshot.documents.mapNotNull { it.getString("memberId") }
                if (memberIds.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .whereIn("userId", memberIds)
                    .get()
                    .addOnSuccessListener { userSnapshot ->
                        val users = userSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
                        onResult(users)
                    }
                    .addOnFailureListener { e ->
                        onResult(emptyList())
                    }
            }
            .addOnFailureListener { e ->
                onResult(emptyList())
            }
    }

    fun unassignMember(taskId: String, memberId: String, onResult: (Boolean, String?) -> Unit) {
        assignedMembersCollection
            .whereEqualTo("taskId", taskId)
            .whereEqualTo("memberId", memberId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onResult(false, "No such assignment found")
                    return@addOnSuccessListener
                }

                // Lấy document đầu tiên (vì 1 task - 1 member chỉ có 1 document)
                val docId = snapshot.documents.first().id

                assignedMembersCollection.document(docId)
                    .delete()
                    .addOnSuccessListener {
                        assignedMembersCollection.whereEqualTo("taskId", taskId).get()
                            .addOnSuccessListener { leftSnap ->
                                if (leftSnap.isEmpty) {
                                    tasksCollection.document(taskId).update("taskStatus", "Open")
                                }
                                onResult(true, null)
                            }
//                        onResult(true, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e.message)
                    }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun updateTaskStatus(taskId: String, newStatus: String, callback: (Result<Unit>) -> Unit) {
        tasksCollection
            .document(taskId)
            .update("taskStatus", newStatus)
            .addOnSuccessListener {
                android.util.Log.d("TaskAdapter", "Task $taskId updated to $newStatus")
                callback(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                android.util.Log.e("TaskAdapter", "Update failed: ${e.message}")
                callback(Result.failure(e))
            }
    }

}