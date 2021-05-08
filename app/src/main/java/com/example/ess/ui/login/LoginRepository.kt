package com.example.ess.ui.login

import android.util.Log
import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository
@Inject constructor(
    private var firebaseAuth: FirebaseAuth,
    private var firebaseDatabase: FirebaseDatabase,
    private val firebaseMessaging: FirebaseMessaging
){
    private val TAG = "Login Repository"


     suspend fun signIn(email:String, password: String): Flow<DataState<*>?> = flow{
        emit(DataState.Loading)
        try {
            val auth = firebaseAuth.signInWithEmailAndPassword(email,password).await()
            val userType = firebaseDatabase.getReference("Users").child(auth.user!!.uid).child("type").get().await()
            if (userType.exists()) emit(DataState.Success(userType.getValue(String::class.java)));
            else emit(DataState.Error(EssError("User dont have a role ! Contact with your admin.",Throwable())))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }
    suspend fun subscribeChannels(){
        val snapshot = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/SubscribedChannels").get().await()
        snapshot.children.forEach {
            firebaseMessaging.subscribeToTopic(it.key as String)
        }
    }
}
