package com.example.ess.ui.admin

import android.util.Log
import androidx.compose.runtime.key
import com.example.ess.model.Notification
import com.example.ess.model.NotificationMapper
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
            firebaseDatabase.getReference("Users").child(auth.user!!.uid).setValue(userType).await()
            emit(DataState.Success(firebaseAuth.currentUser))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

    suspend fun createNotificationChannel(channelName: String): Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        val currentDate = Functions.getCurrentDate()
        try {
            firebaseDatabase.getReference("NotificationChannels/$channelName/$currentDate")
                    .updateChildren(NotificationMapper.modelToMap(Notification(
                        priority = 1,
                        title = "Channel created !",
                        descripton = "created by admin",
                        timestamp = currentDate))).await()
            emit(DataState.Success(channelName))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }
    suspend fun getNotificationChannels(): Flow<DataState<List<String>>> = flow {
        emit(DataState.Loading)
        try {
            val keys = firebaseDatabase.getReference("NotificationChannels").orderByKey().get().await()
            var list = mutableListOf<String>()
            for (i in (keys.value as HashMap<String,Any>)){
                list.add(i.key)
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }
    suspend fun pushNotificationToChannel(channelName: String,notificationTitle: String)= withContext(Dispatchers.IO){
        val currentDate = Functions.getCurrentDate()
        firebaseDatabase.getReference("NotificationChannels/$channelName/$currentDate")
            .updateChildren(NotificationMapper.modelToMap(Notification(
                priority = 1,
                title = notificationTitle,
                descripton = "admin",
                timestamp = currentDate)))
    }
}