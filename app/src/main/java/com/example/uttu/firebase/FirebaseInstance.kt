package com.example.uttu.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseInstance {
    companion object{
//        val firebaseFirestoreInstance = FirebaseFirestore.getInstance()
//
//        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val firebaseFirestoreInstance: FirebaseFirestore by lazy {
            FirebaseFirestore.getInstance()
        }

        val auth: FirebaseAuth by lazy {
            FirebaseAuth.getInstance()
        }
    }
}