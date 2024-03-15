package com.yonasoft.jadedictionary.data.respositories

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.yonasoft.jadedictionary.data.db.word.WordDao

class FirebaseRepository(
    private val firebaseAuth: FirebaseAuth,
    private val authUI: AuthUI,
) {

    fun getAuth (): FirebaseAuth {
        return firebaseAuth
    }
    fun getAuthUI (): AuthUI {
        return authUI
    }
}
