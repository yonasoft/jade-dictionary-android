package com.yonasoft.jadedictionary.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.db.word.WordDatabase
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
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {

        val db = Room.databaseBuilder(context, WordDatabase::class.java, "words.db")
            .createFromAsset("database/words.db")
            .fallbackToDestructiveMigration()
            .build()
        Log.i("db123" ,"db initialized ")
        return db
    }
    @Provides
    @Singleton
    fun provideWordRepository(
        db: WordDatabase,
    ): WordRepository {
        return WordRepository(db.dao())
    }

    @Provides
    @Singleton
    fun provideStoreSearchHistory(@ApplicationContext context: Context): StoreSearchHistory {
        return StoreSearchHistory(context)
    }
}