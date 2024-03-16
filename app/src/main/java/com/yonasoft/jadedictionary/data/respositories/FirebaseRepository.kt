package com.yonasoft.jadedictionary.data.respositories

import android.net.Uri
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val firebaseAuth: FirebaseAuth,
    private val authUI: AuthUI,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) {

    private val user = firebaseAuth.currentUser
    fun getAuth(): FirebaseAuth {
        return firebaseAuth
    }

    fun getAuthUI(): AuthUI {
        return authUI
    }

    fun getFirestore(): FirebaseFirestore {
        return firestore
    }

    fun checkDisplayNameExists(displayName: String, callback: (exists: Boolean) -> Unit) {
        if (displayName == user!!.displayName) callback(false)
        firestore.collection("users").whereEqualTo("displayName", displayName)
            .limit(1).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Firestore", "Error getting documents: $exception")
            }
    }

    suspend fun updateUserDisplayInfo(
        newDisplayName: String?,
        newPhoto: Uri?
    ) {
        var displayName = if (newDisplayName.isNullOrEmpty()) user!!.displayName else newDisplayName
        var photoUrl = user!!.photoUrl.toString()
        if (newPhoto != null) {
            if (photoUrl.isNotBlank()) {
                deleteOldUserProfile()
            }
            photoUrl = uploadNewUserProfile(newPhoto)!!
        }
        updateDisplayInfoInAuth(
            newDisplayName = displayName!!,
            newPhotoUrl = photoUrl,
        )
        updateDisplayInfoInFirestore(
            newDisplayName = displayName!!,
            newPhotoUrl = photoUrl,
        )
    }

    private fun updateDisplayInfoInAuth(
        newDisplayName: String,
        newPhotoUrl: String,
    ) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
            photoUri = Uri.parse(newPhotoUrl)
        }
        user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "User profile updated.")
            }
        }
    }

    private fun updateDisplayInfoInFirestore(
        newDisplayName: String,
        newPhotoUrl: String = user!!.photoUrl.toString()
    ) {
        val uid = firebaseAuth.currentUser!!.uid
        firestore.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.exists()) {
                        // Once the document is found, update displayName and profileURL fields
                        val updates = hashMapOf(
                            "displayName" to newDisplayName,
                            "photoURL" to newPhotoUrl
                        )
                        firestore.collection("users").document(document.id)
                            .update(updates as Map<String, Any>)
                            .addOnSuccessListener {
                                println("Document successfully updated with new displayName and profileURL")
                            }
                            .addOnFailureListener { e ->
                                println("Error updating document: $e")
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    private fun deleteOldUserProfile() {
        val ref = firebaseStorage.getReferenceFromUrl(user!!.photoUrl.toString())

        ref.delete().addOnSuccessListener {
            // File deleted successfully
            Log.d("DeleteProfilePicture", "File deleted successfully")
        }.addOnFailureListener { _ ->
            // Uh-oh, an error occurred!
            Log.d("DeleteProfilePicture", "Failed to delete file")
        }
    }

    private suspend fun uploadNewUserProfile(newPhoto: Uri): String? {
        val storageReference = FirebaseStorage.getInstance().reference
        val userUid = user?.uid ?: return null // Early return if user is null
        val filePath = "$userUid/profile_pictures/${newPhoto.lastPathSegment}"
        val fileRef = storageReference.child(filePath)

        return try {
            fileRef.putFile(newPhoto).await()
            val downloadUrl = fileRef.downloadUrl.await()
            Log.d("UploadProfilePicture", "File uploaded successfully: $downloadUrl")
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("UploadProfilePicture", "Upload failed", e)
            null
        }
    }
}


