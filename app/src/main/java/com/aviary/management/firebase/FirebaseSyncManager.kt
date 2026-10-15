package com.aviary.management.firebase

import android.content.Context
import com.aviary.management.data.Bird
import com.aviary.management.data.DatabaseProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseSyncManager {
    private var firestore: FirebaseFirestore? = null

    fun initialize(context: Context) {
        try {
            FirebaseApp.initializeApp(context)
            firestore = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            // Firebase not configured; caller should add google-services.json and the google services plugin.
            firestore = null
        }
    }

    suspend fun uploadAllBirds(context: Context) {
        val db = DatabaseProvider.get(context)
        val fs = firestore ?: return
        val birds = db.birdDao().observeAll().firstOrNull() ?: return
        withContext(Dispatchers.IO) {
            birds.forEach { b ->
                try {
                    val docId = "bird_${b.id}"
                    val data = mapOf(
                        "localId" to b.id,
                        "name" to b.name,
                        "species" to b.species,
                        "mutation" to b.mutation,
                        "aviaryId" to b.aviaryId,
                        "bandId" to b.bandId,
                        "dob" to (b.dob ?: 0L),
                        "notes" to b.notes,
                        "photoUri" to (b.photoUri ?: ""),
                        "createdAt" to b.createdAt
                    )
                    fs.collection("birds").document(docId).set(data).await()
                } catch (_: Exception) {
                    // ignore individual failures
                }
            }
        }
    }

    suspend fun downloadAndMergeBirds(context: Context) {
        val db = DatabaseProvider.get(context)
        val fs = firestore ?: return
        withContext(Dispatchers.IO) {
            try {
                val snapshot = fs.collection("birds").get().await()
                snapshot.documents.forEach { doc ->
                    try {
                        val name = doc.getString("name") ?: return@forEach
                        val species = doc.getString("species") ?: "Unknown"
                        val mutation = doc.getString("mutation") ?: "Normal"
                        val aviaryId = doc.getLong("aviaryId") ?: 0L
                        val bandId = doc.getString("bandId") ?: ""
                        val dob = doc.getLong("dob")?.takeIf { it > 0 } 
                        val notes = doc.getString("notes") ?: ""
                        val photoUri = doc.getString("photoUri")?.takeIf { it.isNotBlank() }
                        val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

                        // Simple strategy: insert as a new local record. De-duplication is left for future improvements.
                        val bird = Bird(
                            name = name,
                            species = species,
                            mutation = mutation,
                            aviaryId = aviaryId,
                            bandId = bandId,
                            dob = dob,
                            notes = notes,
                            photoUri = photoUri,
                            createdAt = createdAt
                        )
                        db.birdDao().insert(bird)
                    } catch (_: Exception) {
                        // skip malformed documents
                    }
                }
            } catch (_: Exception) {
                // network or permission issue
            }
        }
    }
}
