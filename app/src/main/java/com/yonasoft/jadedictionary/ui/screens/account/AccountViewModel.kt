package com.yonasoft.jadedictionary.ui.screens.account

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.yonasoft.jadedictionary.data.respositories.FirebaseRepository
import com.yonasoft.jadedictionary.ui.screens.account.user_profile.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
) :
    ViewModel() {
    val auth = mutableStateOf(firebaseRepository.getAuth())
    val authUI = mutableStateOf(firebaseRepository.getAuthUI())

    val isEditDisplayName = mutableStateOf(false)
    val currentUser = mutableStateOf(firebaseRepository.getAuth().currentUser)
    val currDisplayName = mutableStateOf(currentUser.value?.displayName ?: "")
    val displayNameField = mutableStateOf(currentUser.value?.displayName ?: "")
    val currentImage = mutableStateOf(currentUser.value?.photoUrl?.toString() ?: "")
    val selectedImage = mutableStateOf<Uri?>(null)

    val email = mutableStateOf("")
    val confirmEmail = mutableStateOf("")
    val emailError = mutableStateOf<String?>(null)


    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
    )

    init {
        firebaseRepository.getAuth().addAuthStateListener { auth ->
            val user = auth.currentUser
            this.auth.value = auth
            currentUser.value = auth.currentUser
            Log.d("AuthStateListener", "Auth state changed. User: ${auth.currentUser?.uid}")
            currDisplayName.value = auth.currentUser?.displayName ?: ""
            currentImage.value = auth.currentUser?.photoUrl?.toString() ?: ""
            if ( user != null) {
                Log.d("AuthStateListener", "User logged in: ${user.uid}")
                // This is a safer place to call your addUserToFirestore method
                firebaseRepository.addUserToFirestore(user)
            }
        }
    }


    fun updateDisplayInfo(
        newDisplayName: String? = displayNameField.value,
        newPhoto: Uri? = selectedImage.value,
        onCheckComplete: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            firebaseRepository.checkDisplayNameExists(newDisplayName!!) { exists ->
                viewModelScope.launch {
                    onCheckComplete(exists)
                    if (!exists) {
                        firebaseRepository.updateUserDisplayInfo(
                            newDisplayName = newDisplayName,
                            newPhoto = newPhoto,
                        )
                    }
                }
            }
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Fetch the current user again to ensure it's not null
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Log.d("sign_in", "Attempting to add user to Firestore.")
                firebaseRepository.addUserToFirestore(user)
            } else {
                Log.d("sign_in", "User is null after sign in.")
            }
        } else {
            Log.d("sign_in", "Sign in failed or cancelled.")
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            try {
                val success = firebaseRepository.signOut(context) {
                    showToast(context, "Signed out!")
                }
            } catch (e: Exception) {
                Log.d("signin", "sign out error: ${e.message.toString()}")
            }
        }
    }
}

