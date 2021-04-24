package com.example.ess.ui.teacher

import android.util.Log
import com.example.ess.model.Notification
import com.example.ess.model.NotificationMapper
import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.example.ess.util.Functions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private var firebaseDatabase: FirebaseDatabase
){

    suspend fun getNotifications(): Flow<DataState<List<Notification>>> = flow {
        emit(DataState.Loading)
        try {
            val user = firebaseAuth.currentUser
            val keys = firebaseDatabase.getReference("Teachers/${user?.uid}/SubscribedChannels").orderByKey().get().await()
            var list = mutableListOf<Notification>()
            for (i in (keys.value as HashMap<String,Any>)){
                val notifications = firebaseDatabase.getReference("NotificationChannels/${i.key}").get().await()
                Log.d("debug", "getNotifications: ${notifications.value}")
                notifications.children.forEach {
                    Log.d("debug", "getNotifications:${it.value} ")
                        list.add(NotificationMapper.mapToModel(it.value as HashMap<String, *>))
                }
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

    suspend fun getAllNotificationChannels(): Flow<DataState<List<String>>> = flow {
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
    suspend fun getUserNotificationChannels(): Flow<DataState<List<String>>> = flow {
        emit(DataState.Loading)
        try {
            val user = firebaseAuth.currentUser
            val keys = firebaseDatabase.getReference("Teachers/${user?.uid}/AuthorizedChannels").orderByKey().get().await()
            var list = mutableListOf<String>()
            for (i in (keys.value as HashMap<String,Any>)){
                list.add(i.key)
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }
    suspend fun pushNotification(channelName: String,notificationTitle: String)= withContext(
        Dispatchers.IO){
        val user = firebaseAuth.currentUser
        val currentDate = Functions.getCurrentDate()
        firebaseDatabase.getReference("NotificationChannels/$channelName/$currentDate")
            .updateChildren(
                NotificationMapper.modelToMap(
                    Notification(
                priority = 1,
                title = notificationTitle,
                descripton = user?.email.toString(),
                timestamp = currentDate)
                ))
    }
    suspend fun subscribeToChannel(channelName: String) = withContext(Dispatchers.IO) {
            val user = firebaseAuth.currentUser
            firebaseDatabase.getReference("Teachers/${user?.uid}/SubscribedChannels/$channelName").setValue(true).await()
    }
}