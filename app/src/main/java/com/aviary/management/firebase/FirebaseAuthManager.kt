package com.aviary.management.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {
    suspend fun signInAnonymously(): Boolean {
        return try {
            Firebase.auth.signInAnonymously().await()
            true
        } catch (_: Exception) {
            false
        }
    }
}
