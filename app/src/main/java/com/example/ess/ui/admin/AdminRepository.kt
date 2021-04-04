package com.example.ess.ui.admin

import com.example.ess.ui.login.AuthError
import com.example.ess.util.DataState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
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
        }catch (cause: AuthError){
            emit(DataState.Error(cause))
        }
    }
}