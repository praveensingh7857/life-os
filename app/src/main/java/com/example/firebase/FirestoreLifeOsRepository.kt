package com.example.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreRepo"

class FirestoreLifeOsRepository(private val context: Context) {

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id))
    }

    private fun requireUserId(): String {
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("User must be authenticated with Google to sync to Firestore")
    }

    suspend fun syncTaskToFirestore(task: TaskEntity) {
        try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("tasks").document(task.id)
            val data = hashMapOf(
                "userId" to uid,
                "title" to task.title,
                "description" to task.description,
                "priority" to task.priority,
                "status" to task.status,
                "category" to task.category,
                "dueDate" to task.dueDate,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d(TAG, "Task synced to Firestore: ${task.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing task to Firestore", e)
        }
    }

    suspend fun syncReminderToFirestore(reminder: ReminderEntity) {
        try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("reminders").document(reminder.id)
            val data = hashMapOf(
                "userId" to uid,
                "title" to reminder.title,
                "time" to reminder.time,
                "date" to reminder.date,
                "category" to reminder.category,
                "isCompleted" to reminder.isCompleted,
                "isRecurring" to reminder.isRecurring,
                "recurrenceRule" to reminder.recurrenceRule,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d(TAG, "Reminder synced to Firestore: ${reminder.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing reminder to Firestore", e)
        }
    }

    suspend fun syncMemoryToFirestore(memory: MemoryEntity) {
        try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("memories").document(memory.id)
            val data = hashMapOf(
                "userId" to uid,
                "title" to memory.title,
                "content" to memory.content,
                "category" to memory.category,
                "isApproved" to memory.isApproved,
                "confidence" to memory.confidence,
                "source" to memory.source,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d(TAG, "Memory synced to Firestore: ${memory.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing memory to Firestore", e)
        }
    }

    suspend fun syncFinanceToFirestore(finance: FinanceEntity) {
        try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("finances").document(finance.id)
            val data = hashMapOf(
                "userId" to uid,
                "title" to finance.title,
                "amount" to finance.amount,
                "type" to finance.type,
                "category" to finance.category,
                "date" to finance.date,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d(TAG, "Finance synced to Firestore: ${finance.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing finance to Firestore", e)
        }
    }
}
