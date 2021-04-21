package com.example.ess.ui.student

import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository
@Inject constructor(private val firebaseDatabase: FirebaseDatabase,
                    private val firebaseAuth: FirebaseAuth){


    suspend fun addChannel(channelName: String) : Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        try {
            val user = firebaseAuth.currentUser
            firebaseDatabase.getReference("Students/${user?.uid}/Channels/$channelName").setValue(true).await()
            emit(DataState.Success("$channelName added to ${user?.email}"))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

    suspend fun getNotificationChannels(): Flow<DataState<List<String>>> = flow {
        emit(DataState.Loading)
        try {
            val keys = firebaseDatabase.getReference("NotificationChannels").orderByKey().get().await()
            val list = mutableListOf<String>()
            (keys.value as HashMap<String,Any>).forEach { i ->
                list.add(i.key)
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }
}