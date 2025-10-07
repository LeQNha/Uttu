package com.example.uttu.repositories

import com.example.uttu.firebase.FirebaseInstance
import com.example.uttu.models.Todo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TodoRepository {

    private val auth: FirebaseAuth = FirebaseInstance.auth
    private val firestore: FirebaseFirestore = FirebaseInstance.firebaseFirestoreInstance


    fun addTodo(taskId: String, todo: Todo, onResult: (Result<String>) -> Unit) {
        val todosRef  = firestore.collection("tasks")
            .document(taskId)
            .collection("todos")
//            .document(todo.todoId) // todoId = documentId luôn

        val docRef = todosRef.document()   // Firestore tự tạo id
        val todoWithId = todo.copy(todoId = docRef.id) // gán id cho todoId

        docRef.set(todoWithId)
            .addOnSuccessListener { onResult(Result.success(docRef.id)) }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }

    // ✅ Lấy danh sách todos của 1 task
    fun getTodos(taskId: String, onResult: (Result<List<Todo>>) -> Unit) {
        firestore.collection("tasks")
            .document(taskId)
            .collection("todos")
            .orderBy("createdAt") // sắp xếp theo thời gian tạo
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onResult(Result.failure(e))
                    return@addSnapshotListener
                }

                val todos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Todo::class.java)
                } ?: emptyList()

                onResult(Result.success(todos))
            }
    }

    fun updateTodoStatus(taskId: String, todoId: String, newStatus: Boolean, onResult: (Result<Void?>) -> Unit) {
        firestore.collection("tasks")
            .document(taskId)
            .collection("todos")
            .document(todoId)
            .update("todoStatus", newStatus)
            .addOnSuccessListener {
                onResult(Result.success(null))
            }
            .addOnFailureListener { e ->
                onResult(Result.failure(e))
            }
    }

    fun deleteTodo(taskId: String, todoId: String, onResult: (Result<Void?>) -> Unit) {
        firestore.collection("tasks")
            .document(taskId)
            .collection("todos")
            .document(todoId)
            .delete()
            .addOnSuccessListener {
                onResult(Result.success(null))
            }
            .addOnFailureListener { e ->
                onResult(Result.failure(e))
            }
    }
}