package com.yonasoft.jadedictionary.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.db.word.WordDatabase
import com.yonasoft.jadedictionary.data.respositories.FirebaseRepository
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideWordRepository(
        db: WordDatabase,
    ): WordRepository {
        return WordRepository(db.dao())
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firebaseAuth: FirebaseAuth,
        authUI: AuthUI,
        fireStore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage
    ): FirebaseRepository {
        return FirebaseRepository(
            firebaseAuth = firebaseAuth,
            authUI = authUI,
            firestore = fireStore,
            firebaseStorage = firebaseStorage,
        )
    }

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {
        val db = Room.databaseBuilder(context, WordDatabase::class.java, "words.db")
            .createFromAsset("database/words.db")
            .fallbackToDestructiveMigration()
            .build()
        Log.i("db123", "db initialized ")
        return db
    }

    @Provides
    @Singleton
    fun provideStoreSearchHistory(@ApplicationContext context: Context): StoreSearchHistory {
        return StoreSearchHistory(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthUI(): AuthUI {
        return AuthUI.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
}