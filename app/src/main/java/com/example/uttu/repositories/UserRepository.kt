package com.example.uttu.repositories

import com.example.uttu.firebase.FirebaseInstance
import com.example.uttu.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task

class UserRepository {

    private val auth: FirebaseAuth = FirebaseInstance.auth
    private val firestore: FirebaseFirestore = FirebaseInstance.firebaseFirestoreInstance

    fun registerUser(
        email: String,
        password: String,
        username: String,
        callback: (Boolean, String?) -> Unit
    ){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val firebaseUser: FirebaseUser? = auth.currentUser
                    firebaseUser?.let { user ->
                        val userId = user.uid
                        val newUser = User(
                            userId = userId,
                            username = username,
                            email = email
                        )

                        firestore.collection("users")
                            .document(userId)
                            .set(newUser)
                            .addOnSuccessListener {
                                callback(true, null)
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.message)
                            }
                    } ?: run{
                        callback(false, "User is null")
                    }
                }else {
                    callback(false, task.exception?.message)
                }
            }

    }

    fun loginUser(email: String, password: String, callback: (Task<AuthResult>) -> Unit){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            callback(task)
        }
    }

    fun searchUsers(query: String, callback:(List<User>?, String?) -> Unit){
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            callback(null, "User not logged in")
            return
        }

        firestore.collection("users")
            .document(currentUserId)
            .collection("friends")
            .get()
            .addOnSuccessListener { friendsSnapshot ->
                val friendIs = friendsSnapshot.documents.map { it.id }.toSet()

                firestore.collection("users")
                    .whereGreaterThanOrEqualTo("username", query)
                    .whereLessThanOrEqualTo("username", query + "\uf8ff")
                    .get()
                    .addOnSuccessListener { result ->
                        val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                            .filter { it.userId != currentUserId && !friendIs.contains(it.userId) }
                        callback(users, null)
                    }
                    .addOnFailureListener {  e ->
                        callback(null, e.message)

                    }
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }

//        firestore.collection("users")
//            .whereGreaterThanOrEqualTo("username", query)
//            .whereLessThanOrEqualTo("username", query + "\uf8ff") // trick cho prefix search
//            .get()
//            .addOnSuccessListener { result ->
//                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
//                callback(users, null)
//            }.addOnFailureListener { e ->
//                callback(null, e.message)
//            }
    }

    fun logout() {
        auth.signOut()
    }
}