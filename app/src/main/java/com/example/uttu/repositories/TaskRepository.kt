package com.example.uttu.repositories

import android.util.Log
import com.example.uttu.firebase.FirebaseInstance
import com.example.uttu.models.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository {

    private val auth: FirebaseAuth = FirebaseInstance.auth
    private val firestore: FirebaseFirestore = FirebaseInstance.firebaseFirestoreInstance

    private val tasksCollection = firestore.collection("tasks")

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
}