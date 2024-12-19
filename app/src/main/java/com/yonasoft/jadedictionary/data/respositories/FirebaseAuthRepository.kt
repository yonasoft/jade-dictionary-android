package com.yonasoft.jadedictionary.data.respositories

import android.net.Uri
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
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

    private fun getFirestore(): FirebaseFirestore {
        return firestore
    }

    fun checkDisplayNameExists(displayName: String, callback: (exists: Boolean) -> Unit) {
        Log.e("change_display", "$displayName ${user!!.displayName}")
        if (displayName == user.displayName) callback(false)
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
        newPhoto: Uri?,
    ) {
        val displayName = if (newDisplayName.isNullOrEmpty()) user!!.displayName else newDisplayName
        var photoUrl = user!!.photoUrl.toString()
        if (newPhoto != null) {
            if (user.photoUrl.toString().isNotEmpty()) {
                deleteOldUserImage()
            }
            photoUrl = uploadNewUserImage(newPhoto)!!
            firestore.collection("users").document(user.uid).update(
                mapOf(
                    "photoURL" to photoUrl,
                    "photoFileName" to newPhoto.lastPathSegment // Or any filename logic you're using
                )
            ).await()

        }
        updateDisplayInfoInAuth(
            newDisplayName = displayName!!,
            newPhotoUrl = photoUrl,
        )
        updateDisplayInfoInFirestore(
            newDisplayName = displayName,
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

    private fun deleteOldUserImage() {
        val photoUrl = user?.photoUrl?.toString()
        if (photoUrl.isNullOrEmpty()) {
            Log.d("DeleteProfilePicture", "No photo URL to delete.")
            return
        }

        try {
            val ref = firebaseStorage.getReferenceFromUrl(photoUrl)
            ref.delete().addOnSuccessListener {
                Log.d("DeleteProfilePicture", "File deleted successfully.")
            }.addOnFailureListener { exception ->
                Log.d("DeleteProfilePicture", "Failed to delete file.", exception)
            }
        } catch (e: IllegalArgumentException) {
            Log.e("DeleteProfilePicture", "The storage Uri could not be parsed: $photoUrl", e)
        }
    }


    private suspend fun uploadNewUserImage(
        newPhoto: Uri
    ): String? {
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
            // Showing a toast directly from the repository is not a best practice. Consider communicating back to UI layer.
            null
        }
    }

    fun addUserToFirestore(currentUser: FirebaseUser = getAuth().currentUser!!) {
        try {
            Log.d(
                "sign_in",
                "Attempting to add/update user in Firestore for UID: ${currentUser.uid}"
            )
            val uid = currentUser.uid
            val userMap = hashMapOf(
                "displayName" to (currentUser.displayName ?: "N/A"),
                "email" to (currentUser.email ?: "N/A"),
                "photoFileName" to "",
                "photoURL" to (currentUser.photoUrl?.toString() ?: "N/A"),
                "uid" to uid
            )
            firestore.collection("users").document(uid).set(userMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("firestore_add", "Successfully written user document for UID: $uid")
                }
                .addOnFailureListener { e ->
                    Log.w(
                        "firestore_add",
                        "Error writing document for UID: $uid. Error: ${e.message}",
                        e
                    )
                }
        } catch (e: Exception) {
            Log.e("firestore_add", "Exception in addUserToFirestore", e)
        }
    }

    fun updatePassword(newPassword: String, onComplete: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true, null)
            } else {
                if (task.exception is FirebaseAuthRecentLoginRequiredException) {
                    // This exception indicates the user must re-authenticate.
                    onComplete(false, "Please re-login to update your password.")
                } else {
                    // Other exceptions
                    onComplete(false, task.exception?.message ?: "An error occurred")
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message ?: "An unknown error occurred")
                }
            }
    }

    suspend fun deleteUserAccount(user: FirebaseUser? = FirebaseAuth.getInstance().currentUser): Result<Boolean> {
        user.let { user ->
            return try {
                deleteOldUserImage()
                deleteAllUserWordLists()
                firestore.collection("users").document(user!!.uid).delete().await()
                firebaseAuth.currentUser?.delete()?.await()

                Result.success(true)
            } catch (e: Exception) {
                Log.e("FirebaseRepository", "Error deleting user account: $e")
                Result.failure(e)
            }
        }
    }

    suspend fun deleteAllUserWordLists(user: FirebaseUser? = FirebaseAuth.getInstance().currentUser) {
        if (user != null) {
            val wordListsRef = FirebaseFirestore.getInstance().collection("wordLists")
            wordListsRef.whereEqualTo("userUid", user.uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Iterate through the documents and delete them individually
                    for (document in task.result) {
                        wordListsRef.document(document.id).delete().addOnSuccessListener {
                            Log.d("DeleteDocument", "DocumentSnapshot successfully deleted!")
                        }.addOnFailureListener { e ->
                            Log.w("DeleteDocument", "Error deleting document", e)
                        }
                    }
                } else {
                    Log.d("QueryError", "Error getting documents: ", task.exception)
                }
            }
        }

    }


    fun signOut() {
        FirebaseAuth.getInstance()
            .signOut()
    }
}





