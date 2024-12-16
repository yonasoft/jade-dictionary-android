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
import com.google.firebase.auth.FirebaseUser
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
    val networkAvailable = mutableStateOf(true)
    val auth = mutableStateOf<FirebaseAuth?>(firebaseAuthRepository.getAuth())
    val currentUser = mutableStateOf<FirebaseUser?>(null)
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
        auth.value!!.addAuthStateListener {
            auth.value = it
            currentUser.value = it.currentUser
            it.currentUser?.let { user ->
                val name = user.displayName ?: ""
                currDisplayName.value = name
                displayNameField.value = name
            }
        }
    }

    fun updateDisplayInfo(
        newDisplayName: String? = displayNameField.value,
        newPhoto: Uri? = selectedImage.value,
    ) {
        var message = ""
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseAuthRepository.updateUserDisplayInfo(
                    newDisplayName = newDisplayName,
                    newPhoto = newPhoto,
                )
                currDisplayName.value = newDisplayName ?: currDisplayName.value
                currentUser.value!!.reload()
                message = "Display info successfully changed!"
            } catch (e: Exception) {
                message = "Error while changing display info: $e"
            } finally {
                //Check if there is a change in name then if that new name already exists.
                if (displayNameField.value.isNotEmpty() && displayNameField.value != currentUser.value!!.displayName){
                    firebaseAuthRepository.checkDisplayNameExists(newDisplayName!!) {
                        if (it) {
                            message = "Display name already exists!"
                        }
                    }
                }
                withContext(Dispatchers.Main){
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

    fun initiateAccountDeletion(onComplete: ((Boolean, String?) -> Unit?)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = firebaseAuthRepository.deleteUserAccount()
            if (result.isSuccess) {
                if (!currentUser.value!!.isAnonymous) showToast(
                    context, "Account successfully deleted.", Toast.LENGTH_LONG
                )
            } else {
                val exception = result.exceptionOrNull()
                if (exception is FirebaseAuthRecentLoginRequiredException) {
                    showToast(context, "Please re-login to delete your account.", Toast.LENGTH_LONG)
                    if (onComplete != null) onComplete(
                        false, "Please re-login to delete your account."
                    )
                } else {
                    if (!currentUser.value!!.isAnonymous) showToast(
                        context,
                        exception?.message ?: "An error occurred during account deletion.",
                        Toast.LENGTH_LONG
                    )
                    if (onComplete != null) onComplete(false, exception?.message)
                }
            }
            auth.value!!.currentUser?.reload()
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        Log.d("signin", "got here...${response?.error}")
        Log.d("signin", "got here...${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            Log.d("auth logged in through auth ui", auth.value!!.currentUser!!.uid)
            auth.value!!.currentUser?.reload()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.value!!.currentUser?.reload()
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

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = firebaseAuthRepository.signOut(context) {
                    auth.value!!.currentUser?.reload()
                    showToast(context, "Signed out!")
                }
            } catch (e: Exception) {
                Log.d("signout", "sign out error: ${e.message.toString()}")
            }
        }
    }
}

