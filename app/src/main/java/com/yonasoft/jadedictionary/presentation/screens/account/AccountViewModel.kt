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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
) : ViewModel() {

    val showForgotPasswordDialog = mutableStateOf(false)

    val isEditDisplayName = mutableStateOf(false)
    val currDisplayName = mutableStateOf("")
    val displayNameField = mutableStateOf(currDisplayName.value)
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

    }

    fun updateDisplayInfo(
        newDisplayName: String? = displayNameField.value,
        newPhoto: Uri? = selectedImage.value,
        auth: FirebaseAuth,
    ) {
        var message = ""
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseAuthRepository.updateUserDisplayInfo(
                    newDisplayName = newDisplayName,
                    newPhoto = newPhoto,
                )
                currDisplayName.value = newDisplayName ?: currDisplayName.value
                message = "Display info successfully changed!"
            } catch (e: Exception) {
                message = "Error while changing display info: $e"
            } finally {
                //Check if there is a change in name then if that new name already exists.
                if (displayNameField.value.isNotEmpty() && displayNameField.value != auth.currentUser!!.displayName) {
                    firebaseAuthRepository.checkDisplayNameExists(newDisplayName!!) {
                        if (it) {
                            message = "Display name already exists!"
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    showToast(context = context, message = message)
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
            viewModelScope.launch(Dispatchers.IO) {
                firebaseAuthRepository.updatePassword(password.value) { success, errorMessage ->
                    if (success) {
                        showToast(context, "Password updated successfully")
                    } else {
                        showToast(context, errorMessage ?: "Failed to update password")
                    }
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            showToast(context, "Please enter your email address.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuthRepository.sendPasswordResetEmail(email) { success, message ->
                if (success) {
                    showToast(context, "Reset link sent to your email.")
                } else {
                    showToast(context, message ?: "Failed to send reset link.")
                }
            }
        }
    }

    fun initiateAccountDeletion(
        auth: FirebaseAuth,
        onComplete: ((Boolean, String?) -> Unit?)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = firebaseAuthRepository.deleteUserAccount()
            if (result.isSuccess) {
                if (auth.currentUser!!.isAnonymous) showToast(
                    context, "Account successfully deleted.", Toast.LENGTH_LONG
                )
            } else {
                val exception = result.exceptionOrNull()
                if (exception is FirebaseAuthRecentLoginRequiredException) {
                    showToast(
                        context,
                        "Please re-login to delete your account.",
                        Toast.LENGTH_LONG
                    )
                    if (onComplete != null) onComplete(
                        false, "Please re-login to delete your account."
                    )
                } else {
                    if (auth.currentUser!!.isAnonymous) showToast(
                        context,
                        exception?.message ?: "An error occurred during account deletion.",
                        Toast.LENGTH_LONG
                    )
                    if (onComplete != null) onComplete(false, exception?.message)
                }
            }
        }
    }

    fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult,
        auth: FirebaseAuth,
        updateAuth: () -> Unit
    ) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            updateAuth()
            auth.currentUser?.let { user ->
                Log.d("Auth", "User signed in: ${user.uid}")
                // Update other UI-related states (if necessary)
                currDisplayName.value = user.displayName ?: ""
                displayNameField.value = user.displayName ?: ""
            }
            showToast(context, "Successfully signed in!")
        } else {
            // Handle sign-in failure
            val error = response?.error?.errorCode
            Log.e("Auth", "Sign-in failed. Error code: $error")
            showToast(context, "Sign-in failed. Please try again.")
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                    } else {
                        Log.w("Auth", "signInAnonymously:failure", task.exception)
                        showToast(context, "Authentication failed.")
                    }
                }
            } catch (e: Exception) {
                showToast(context, "Authentication failed. Exception: ${e.message}")
            }
        }
    }

    fun signOut(updateAuth: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseAuthRepository.signOut()
                Log.d("signOut", "User signed out successfully")
                withContext(Dispatchers.Main) {
                    updateAuth() // Explicitly call to update the shared state
                    showToast(context, "Signed out!")
                }
            } catch (e: Exception) {
                Log.e("signOut", "Error signing out: ${e.message}")
            }
        }
    }
}

