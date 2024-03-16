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
import com.yonasoft.jadedictionary.data.respositories.FirebaseRepository
import com.yonasoft.jadedictionary.ui.screens.account.user_profile.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            this.auth.value = auth
            currentUser.value = auth.currentUser
            currDisplayName.value = auth.currentUser?.displayName ?: ""
            currentImage.value = auth.currentUser?.photoUrl?.toString() ?: ""
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
        viewModelScope.launch {
            val response = result.idpResponse
            Log.d("signin", "got here...${response?.error}")
            Log.d("signin", "got here...${result.resultCode}")
            if (result.resultCode == RESULT_OK) {
                currentUser.value = auth.value.currentUser
                firebaseRepository.addUserToFirestore()
                Log.d("signin", currentUser.value!!.uid)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            try {
                val success = firebaseRepository.signOut(context){
                    showToast(context, "Signed out!")
                }
            } catch (e: Exception) {
                Log.d("signin", "sign out error: ${e.message.toString()}")
            }
        }
    }
}

