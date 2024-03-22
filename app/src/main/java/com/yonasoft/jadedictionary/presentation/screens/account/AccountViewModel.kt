package com.yonasoft.jadedictionary.presentation.screens.account

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.yonasoft.jadedictionary.data.respositories.FirebaseAuthRepository
import com.yonasoft.jadedictionary.presentation.screens.account.user_profile.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
) :
    ViewModel() {
    val networkAvailable = mutableStateOf(true)
    val auth = mutableStateOf<FirebaseAuth?>(null)
    val authUI = mutableStateOf(firebaseAuthRepository.getAuthUI())
    val showForgotPasswordDialog = mutableStateOf(false)

    val isEditDisplayName = mutableStateOf(false)
    val currentUser = mutableStateOf(firebaseAuthRepository.getAuth().currentUser)
    val currDisplayName = mutableStateOf(currentUser.value?.displayName ?: "")
    val displayNameField = mutableStateOf(currentUser.value?.displayName ?: "")
    val currentImage = mutableStateOf(currentUser.value?.photoUrl?.toString() ?: "")
    val selectedImage = mutableStateOf<Uri?>(null)

    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")
    val passwordError = mutableStateOf<String?>(null)
    val passwordVisible = mutableStateOf(false)

    val showDeletionConfirmation = mutableStateOf(false)
    val confirmationText = mutableStateOf("")
    val showDeleteConfirmationError = mutableStateOf(false)

    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
    )

    init {
        firebaseAuthRepository.getAuth().addAuthStateListener {
            viewModelScope.launch {
                val user = it.currentUser
                if (user != null) {
                    auth.value = it
                    currentUser.value = user
                    Log.d("AuthStateListener", "Auth state changed. User: ${user?.uid}")
                    currDisplayName.value = user?.displayName ?: ""
                    currentImage.value = user?.photoUrl?.toString() ?: ""
                    Log.d("AuthStateListener", "User logged in: ${user.uid}")
                    // Safely call addUserToFirestore method after user login is confirmed
                    firebaseAuthRepository.addUserToFirestore(user)
                } else {
                }
            }
        }
    }

    fun updateDisplayInfo(
        newDisplayName: String? = displayNameField.value,
        newPhoto: Uri? = selectedImage.value,
        onCheckComplete: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            firebaseAuthRepository.checkDisplayNameExists(newDisplayName!!) { exists ->
                viewModelScope.launch {
                    onCheckComplete(exists)
                    if (!exists) {
                        firebaseAuthRepository.updateUserDisplayInfo(
                            newDisplayName = newDisplayName,
                            newPhoto = newPhoto,
                        ) {
                            showToast(context = context, message = it)
                        }
                    }
                }
            }
        }
    }

    private fun validatePassword(password: String, confirmPassword: String): String? {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            return "Password cannot be empty."
        }
        if (password.length < 8) {
            return "Password must be at least 8 characters long."
        }
        if (!password.any { it.isDigit() } || !password.any { it.isLetter() }) {
            return "Password must contain both letters and numbers."
        }
        if (password != confirmPassword) {
            return "Passwords do not match."
        }
        return null // null indicates no error
    }

    fun savePassword() {
        passwordError.value = validatePassword(password.value, confirmPassword.value)
        if (passwordError.value == null) {
            firebaseAuthRepository.updatePassword(password.value) { success, errorMessage ->
                if (success) {
                    showToast(context, "Password updated successfully")
                } else {
                    showToast(context, errorMessage ?: "Failed to update password")
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            showToast(context, "Please enter your email address.")
            return
        }

        firebaseAuthRepository.sendPasswordResetEmail(email) { success, message ->
            if (success) {
                showToast(context, "Reset link sent to your email.")
            } else {
                showToast(context, message ?: "Failed to send reset link.")
            }
        }
    }

    fun initiateAccountDeletion(onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = firebaseAuthRepository.deleteUserAccount()
            if (result.isSuccess) {
                showToast(context, "Account successfully deleted.", Toast.LENGTH_LONG)
                onComplete(true, null)
            } else {
                val exception = result.exceptionOrNull()
                if (exception is FirebaseAuthRecentLoginRequiredException) {
                    showToast(context, "Please re-login to delete your account.", Toast.LENGTH_LONG)
                    onComplete(false, "Please re-login to delete your account.")
                } else {
                    showToast(
                        context,
                        exception?.message ?: "An error occurred during account deletion.",
                        Toast.LENGTH_LONG
                    )
                    onComplete(false, exception?.message)
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
                firebaseAuthRepository.addUserToFirestore(user)
            } else {
                Log.d("sign_in", "User is null after sign in.")
            }
        } else {
            Log.d("sign_in", "Sign in failed or cancelled.")
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign-in success, update the UI with the signed-in user's information
                            Log.d("AccountViewModel", "signInAnonymously:success")
                            currentUser.value = FirebaseAuth.getInstance().currentUser
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AccountViewModel", "signInAnonymously:failure", task.exception)
                            showToast(context, "Authentication failed.")
                        }
                    }
            } catch (e: Exception) {
                Log.e("AccountViewModel", "signInAnonymously:exception", e)
                showToast(context, "Authentication failed. Exception: ${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                val success = firebaseAuthRepository.signOut(context) {
                    showToast(context, "Signed out!")
                }
            } catch (e: Exception) {
                Log.d("signout", "sign out error: ${e.message.toString()}")
            }
        }
    }

}

