package com.example.ess.ui.admin

import android.util.Log
import androidx.compose.runtime.key
import com.example.ess.model.Notification
import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.example.ess.util.Functions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository
    @Inject constructor(
        private val firebaseAuth: FirebaseAuth,
        private var firebaseDatabase: FirebaseDatabase
    ) {


    suspend fun signUp(email: String,password: String,userType: String): Flow<DataState<*>> = flow{
        emit(DataState.Loading)
        try {
            val auth = firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            firebaseDatabase.getReference("Users").child(auth.user!!.uid).child("type").setValue(userType).await()
            emit(DataState.Success(firebaseAuth.currentUser))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

    suspend fun createNotificationChannel(className: String): Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        val currentDate = Functions.getCurrentDate()
        try {
            val not = Notification("root",className,"9oba9GFOvtZ6wvX4vonlUzJVEUa2","https://t3.ftcdn.net/jpg/03/62/56/24/360_F_362562495_Gau0POzcwR8JCfQuikVUTqzMFTo78vkF.jpg",
            "Class created!","",currentDate.toString())
            firebaseDatabase.getReference("Notifications/$className").push()
                    .setValue(not).await()
            emit(DataState.Success(className))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }
    suspend fun getNotificationChannels(): Flow<DataState<List<String>>> = flow {
        emit(DataState.Loading)
        try {
            val keys = firebaseDatabase.getReference("Classes").orderByKey().get().await()
            val list = mutableListOf<String>()
            keys.children.forEach {
                it.key?.let { it1 -> list.add(it1) }
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }
    suspend fun pushNotificationToChannel(channelName: String,notificationTitle: String)= withContext(Dispatchers.IO){
        val currentDate = Functions.getCurrentDate()
        firebaseDatabase.getReference("NotificationChannels/$channelName").push()
            .setValue(Notification("root",channelName,"9oba9GFOvtZ6wvX4vonlUzJVEUa2","https://t3.ftcdn.net/jpg/03/62/56/24/360_F_362562495_Gau0POzcwR8JCfQuikVUTqzMFTo78vkF.jpg",
            notificationTitle,"",currentDate.toString()))
    }
}