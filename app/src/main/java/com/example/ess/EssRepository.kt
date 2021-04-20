package com.example.ess

import android.util.Log
import com.example.ess.model.Notification
import com.example.ess.model.NotificationMapper
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EssRepository
@Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
){
    companion object {
        const val TAG = "Ess Repository"
    }

    suspend fun insertNotificationToFirebase(channel: String, notification: Notification) = withContext(Dispatchers.IO){
        firebaseDatabase.getReference(channel).child(notification.timestamp.toString()).updateChildren(NotificationMapper.modelToMap(notification)).
        addOnCompleteListener {
            if (it.isSuccessful) Log.d(TAG, "insertNotificationToFirebase: success ${it.result}") else
                Log.e(TAG, "insertNotificationToFirebase: ${it.exception!!.message}")
        }
    }

}