package com.example.ess.ui.common

import com.example.ess.model.Message
import com.example.ess.model.MessageMapper
import com.example.ess.model.UserShort
import com.example.ess.model.UserShortMapper
import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.example.ess.util.Functions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private var firebaseDatabase: FirebaseDatabase
){

    suspend fun getUsers(query: String): Flow<DataState<List<UserShort>>> = flow {
        emit(DataState.Loading)
        try {
            val list = mutableListOf<UserShort>()
            val users = firebaseDatabase.getReference("Users").orderByChild("name").equalTo(query).get().await()
            users.children.forEach {
                list.add(UserShortMapper.mapToModel(it.value as HashMap<String, *>))
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }


    suspend fun createMessaging(receiverUid:String) = withContext(Dispatchers.IO){

        val chatID = firebaseDatabase.getReference("Messages").push()
        //setting data for message sender
        firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/Messages/$receiverUid").setValue(chatID.key)
        //setting data for message receiver
        firebaseDatabase.getReference("Users/$receiverUid/Messages/${firebaseAuth.currentUser!!.uid}").setValue(chatID.key)
        return@withContext chatID.key
    }

    suspend fun sendMessage(chatID: String,message: String){
        firebaseDatabase.getReference("Messages/$chatID").push().updateChildren(
                MessageMapper.modelToMap(
                        Message(
                                timestamp = Functions.getCurrentDate().toString(),
                                message = message
                        )
                )
        )
    }

}