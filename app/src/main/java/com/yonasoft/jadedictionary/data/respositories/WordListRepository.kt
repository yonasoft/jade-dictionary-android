package com.yonasoft.jadedictionary.data.respositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.yonasoft.jadedictionary.data.models.WordList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class WordListRepository(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    fun getWordLists(): Flow<List<WordList>> = callbackFlow {
        if (firebaseAuth.currentUser != null) {
            val wordListsCollection = firestore.collection("wordLists")
            val userUid: String? = firebaseAuth.currentUser?.uid
            val listenerRegistration = wordListsCollection.whereEqualTo("userUid", userUid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val wordLists =
                        snapshot?.documents?.mapNotNull { documentToWordList(it) }.orEmpty()
                    trySend(wordLists)
                }

            awaitClose { listenerRegistration.remove() }
        }
    }

    suspend fun getWordListById(firebaseId: String): WordList? {

        val wordListsCollection = firestore.collection("wordLists")
        return try {
            val documentSnapshot = wordListsCollection.document(firebaseId).get().await()
            if (documentSnapshot.exists()) {
                documentToWordList(documentSnapshot)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun searchWordLists(query: String): Flow<List<WordList>> = callbackFlow {
        val wordListsCollection = firestore.collection("wordLists")
        val userUid: String? = firebaseAuth.currentUser?.uid
        val queryLowerCase = query.lowercase()
        val listenerRegistration = wordListsCollection
            .whereEqualTo("userUid", userUid)
            .orderBy("title")
            .startAt(queryLowerCase)
            .endAt("$queryLowerCase\uf8ff")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val wordLists = snapshot?.documents?.mapNotNull { documentToWordList(it) }.orEmpty()
                trySend(wordLists)
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun addOrUpdateWordList(wordList: WordList): WordList {
        val wordListsCollection = firestore.collection("wordLists")
        val wordListMap = wordList.toMap()
        return if (wordList.firebaseId == null) {
            val addedDocRef = wordListsCollection.add(wordListMap).await()
            wordList.copy(firebaseId = addedDocRef.id)
        } else {
            wordList.firebaseId.let {
                wordListsCollection.document(it).set(wordListMap).await()
            }
            wordList
        }
    }

    suspend fun deleteWordList(firebaseId: String?) {
        val wordListsCollection = firestore.collection("wordLists")
        firebaseId?.let {
            wordListsCollection.document(it).delete().await()
        }
    }

    private fun documentToWordList(document: DocumentSnapshot): WordList {
        val wordIds = (document.get("wordIds") as? List<Number>)?.map(Number::toLong) ?: emptyList()
        return WordList(
            firebaseId = document.id,
            title = document.getString("title") ?: "",
            description = document.getString("description"),
            createdAt = document.getDate("createdAt") ?: Date(),
            lastUpdatedAt = document.getDate("lastUpdatedAt") ?: Date(),
            userUid = document.getString("userUid") ?: "",
            wordIds = wordIds
        )
    }

    private fun WordList.toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "description" to (description ?: ""),
            "createdAt" to createdAt,
            "lastUpdatedAt" to lastUpdatedAt,
            "userUid" to userUid,
            "wordIds" to wordIds
        )
    }
}
