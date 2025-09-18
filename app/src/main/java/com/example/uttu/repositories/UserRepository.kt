package com.example.uttu.repositories

import com.example.uttu.firebase.FirebaseInstance
import com.example.uttu.models.SearchUserResult
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

//    fun searchUsers(query: String, callback:(List<User>?, String?) -> Unit){
//        val currentUserId = auth.currentUser?.uid
//        if (currentUserId == null) {
//            callback(null, "User not logged in")
//            return
//        }
//
//        firestore.collection("users")
//            .document(currentUserId)
//            .collection("friends")
//            .get()
//            .addOnSuccessListener { friendsSnapshot ->
//                val friendIs = friendsSnapshot.documents.map { it.id }.toSet()
//
//                firestore.collection("users")
//                    .whereGreaterThanOrEqualTo("username", query)
//                    .whereLessThanOrEqualTo("username", query + "\uf8ff")
//                    .get()
//                    .addOnSuccessListener { result ->
//                        val users = result.documents.mapNotNull { it.toObject(User::class.java) }
//                            .filter { it.userId != currentUserId && !friendIs.contains(it.userId) }
//                        callback(users, null)
//                    }
//                    .addOnFailureListener {  e ->
//                        callback(null, e.message)
//
//                    }
//            }
//            .addOnFailureListener { e ->
//                callback(null, e.message)
//            }

    fun searchUsers(query: String, callback:(List<SearchUserResult>?, String?) -> Unit){
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

                firestore.collection("friendRequesteds")
                    .whereEqualTo("requestSenderId", currentUserId)
                    .get()
                    .addOnSuccessListener { requestsSnapshot ->
                        val requestedIds = requestsSnapshot.documents.mapNotNull { it.getString("requestReceiverId") }.toSet()
                        firestore.collection("users")
                            .whereGreaterThanOrEqualTo("username", query)
                            .whereLessThanOrEqualTo("username", query + "\uf8ff")
                            .get()
                            .addOnSuccessListener { result ->
                                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                                    .filter { it.userId != currentUserId && !friendIs.contains(it.userId) }
                                    .map { user ->
                                        SearchUserResult(user, requestedIds.contains(user.userId))
                                    }
                                callback(users, null)
                            }
                            .addOnFailureListener {  e ->
                                callback(null, e.message)

                            }
                    }
                    .addOnFailureListener { e ->
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

    fun sendFriendRequest(receiverId: String, callback: (Boolean, String?) -> Unit){
        val senderId = auth.currentUser?.uid
        if(senderId == null){
            callback(false, "User not logged in")
            return
        }
        val docRef = firestore.collection("friendRequesteds").document()
        val requestId = docRef.id

        val request = hashMapOf(
            "requestId" to requestId,
            "requestSenderId" to senderId,
            "requestReceiverId" to receiverId,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        docRef.set(request)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun cancelFriendRequest(receiverId: String, callback: (Boolean, String?) -> Unit){
        val senderId = auth.currentUser?.uid
        if(senderId == null){
            callback(false, "User not logged in")
            return
        }

        firestore.collection("friendRequesteds")
            .whereEqualTo("requestSenderId", senderId)
            .whereEqualTo("requestReceiverId", receiverId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    callback(false, "No request found")
                    return@addOnSuccessListener
                }

                // xóa tất cả các request trùng (thường chỉ có 1)
                val batch = firestore.batch()
                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        callback(true, null)
                    }
                    .addOnFailureListener { e ->
                        callback(false, e.message)
                    }
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun getIncomingFriendRequests(callback: (List<User>?, String?) -> Unit){
        val currentUserId = auth.currentUser?.uid
        if(currentUserId == null){
            callback(null, "User not logged in")
            return
        }

        firestore.collection("friendRequesteds")
            .whereEqualTo("requestReceiverId", currentUserId)
            .get()
            .addOnSuccessListener { requestSnapshot ->
                val senderIds = requestSnapshot.documents.mapNotNull { it.getString("requestSenderId") }

                if (senderIds.isEmpty()) {
                    callback(emptyList(), null)
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .whereIn("userId", senderIds)
                    .get()
                    .addOnSuccessListener { userSnapshot ->
                        val users = userSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
                        callback(users, null)
                    }
                    .addOnFailureListener { e->
                        callback(null, e.message)
                    }
            }
            .addOnFailureListener { e->
                callback(null, e.message)
            }
    }

    fun declineFriendRequest(requestSenderId: String, callback: (Boolean, String?) -> Unit){
        val currentUserId = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }

        firestore.collection("friendRequesteds")
            .whereEqualTo("requestSenderId", requestSenderId)
            .whereEqualTo("requestReceiverId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if(querySnapshot.isEmpty){
                    callback(false, "Request not found")
                }else{
                    val batch = firestore.batch()
                    for (doc in querySnapshot.documents) {
                        batch.delete(doc.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener { callback(true, null) }
                        .addOnFailureListener { e -> callback(false, e.message) }
                }
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun acceptFriendRequests(senderId: String, callback: (Boolean, String?) -> Unit){
        val currentUserId = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }

        firestore.collection("friendRequesteds")
            .whereEqualTo("requestSenderId", senderId)
            .whereEqualTo("requestReceiverId", currentUserId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                for (doc in snapshot.documents){
                    batch.delete(doc.reference)
                }

                val currentUserFriendRef = firestore.collection("users")
                    .document(currentUserId)
                    .collection("friends")
                    .document(senderId)
                batch.set(currentUserFriendRef, mapOf("userId" to senderId))

                // Thêm vào friends của sender (chỉ lưu userId)
                val senderFriendRef = firestore.collection("users")
                    .document(senderId)
                    .collection("friends")
                    .document(currentUserId)
                batch.set(senderFriendRef, mapOf("userId" to currentUserId))

                batch.commit()
                    .addOnSuccessListener { callback(true, null) }
                    .addOnFailureListener { e -> callback(false, e.message) }
            }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun getFriends(callback: (List<User>?, String?) -> Unit){
        val currentUserId = auth.currentUser?.uid ?: run {
            callback(null, "User not logged in")
            return
        }
        firestore.collection("users")
            .document(currentUserId)
            .collection("friends")
            .get()
            .addOnSuccessListener { snapshot ->
                val friendIds = snapshot.documents.mapNotNull { it.getString("userId") }

                if (friendIds.isEmpty()) {
                    callback(emptyList(), null)
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .whereIn("userId", friendIds)
                    .get()
                    .addOnSuccessListener { usersSnapshot ->
                        val friends = usersSnapshot.toObjects(User::class.java)
                        callback(friends, null)
                    }
                    .addOnFailureListener { e -> callback(null, e.message) }
            }
            .addOnFailureListener { e -> callback(null, e.message) }
    }

    fun logout() {
        auth.signOut()
    }
}