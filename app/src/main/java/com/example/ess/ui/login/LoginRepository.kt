package com.example.ess.ui.login

import android.util.Log
import com.example.ess.util.DataState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository
@Inject constructor(
    private var firebaseAuth: FirebaseAuth,
    private var firebaseDatabase: FirebaseDatabase){
    private val TAG = "Login Repository"

    suspend fun signUp(email:String, password: String): Flow<DataState<FirebaseUser?>?> = flow{
        emit(DataState.Loading)
        try {
            firebaseAuth.createUserWithEmailAndPassword(email,password)
            emit(DataState.Success(firebaseAuth.currentUser))
        }catch (cause:AuthError){
            throw AuthError("Signup failed !", cause)
        }
    }

     fun signIn(email:String, password: String): Flow<DataState<*>?> = flow{
        Log.d(TAG, "signIn: here")
        emit(DataState.Loading)
        try {
            Log.d(TAG, "signIn: try")
            val auth = firebaseAuth.signInWithEmailAndPassword(email,password).await()
            val userType = firebaseDatabase.getReference("Users").child(auth.user!!.uid).get().await()
            if (userType.exists()) emit(DataState.Success(userType.getValue(String::class.java)));
            else emit(DataState.Error(AuthError("User dont have a role ! Contact with your admin.",Throwable())))
            /*Log.d(
                TAG,
                "signIn: ${userType.getValue(String::class.java)}"
            )*/

        }catch (cause:AuthError){
            Log.d(TAG, "signIn: catch")
            emit(DataState.Error(cause))
        }


    }
}
class AuthError(message: String, cause: Throwable): Throwable(message,cause)