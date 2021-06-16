package com.example.ess.ui.student

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.example.ess.model.*
import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.example.ess.util.Functions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository
@Inject constructor(private val firebaseDatabase: FirebaseDatabase,
                    private val firebaseAuth: FirebaseAuth,
                    private val firebaseStorage: FirebaseStorage) : StudentRepoImpl {


    suspend fun getUserInfo() = withContext(Dispatchers.IO) {
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
        val user = ss.getValue(User::class.java)
        Log.d("debug", "getUserInfo: ${user!!.imageUrl}")
        return@withContext user

    }

    suspend fun addChannel(channelName: String): Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        try {
            val user = firebaseAuth.currentUser
            firebaseDatabase.getReference("Students/${user?.uid}/Channels/$channelName").setValue(true).await()
            emit(DataState.Success("$channelName added to ${user?.email}"))
        } catch (cause: EssError) {
            emit(DataState.Error(cause))
        }
    }

    suspend fun getNotificationChannels(): Flow<DataState<List<String>>> = flow {
        emit(DataState.Loading)
        try {
            val keys = firebaseDatabase.getReference("NotificationChannels").orderByKey().get().await()
            val list = mutableListOf<String>()
            (keys.value as HashMap<String, Any>).forEach { i ->
                list.add(i.key)
            }
            emit(DataState.Success(list))
        } catch (cause: EssError) {
            emit(DataState.Error(cause))
        }
    }

    suspend fun getClassList() = withContext(Dispatchers.IO) {
        val uid = firebaseAuth.currentUser!!.uid
        val list = mutableListOf<String>()
        val snapshot = firebaseDatabase.getReference("Users/$uid/Classes").orderByKey().get().await()
        snapshot.children.forEach {
            it.key?.let { it1 -> list.add(it1) }
        }
        return@withContext list
    }

    suspend fun getIssueList(className: String) = withContext(Dispatchers.IO) {
        val list = mutableListOf<String>()
        val snapshot = firebaseDatabase.getReference("Classes/$className").orderByChild("title").get().await()
        snapshot.children.forEach {
            list.add(it.child("title").value as String)
        }
        return@withContext list
    }

    suspend fun deleteSubmission(submit: Submit): Any = withContext(Dispatchers.IO) {
        firebaseDatabase.getReference(submit.path).child("submits").child(submit.uid).removeValue().await()
    }

    @SuppressLint("RestrictedApi")
    suspend fun checkSubmissions(className: String, issueName: String): Flow<DataState<Submit?>> = flow {
        emit(DataState.Loading)
        try {
            val snapshot = firebaseDatabase.getReference("Classes").child(className).orderByChild("title").equalTo(issueName).get().await()
            var path: String? = null
            snapshot.children.forEach {
                path = it.ref.path.wireFormat()
            }
            val ss2 = firebaseDatabase.getReference(path!!).child("submits").orderByKey()
                    .equalTo(firebaseAuth.currentUser?.uid).get().await()
            var submit: Submit? = null
            ss2.children.forEach {
                submit = it.getValue(Submit::class.java)
            }
            Log.d("TAG", "checkSubmissions: $submit")
            if (submit != null) {
                emit(DataState.Success(submit))
            } else {
                emit(DataState.Empty)
            }

        } catch (cause: EssError) {
            emit(DataState.Error(cause))
        }
    }

    @SuppressLint("RestrictedApi")
    @ExperimentalCoroutinesApi
    override fun submitToIssue(
            uri: Uri,
            className: String,
            issueName: String,
            fileName: String
    ): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val file = firebaseStorage.getReference("Classes/$className/$issueName/${firebaseAuth.currentUser!!.uid}/$fileName")
        val listener = OnProgressListener<UploadTask.TaskSnapshot> {
            offer(DataState.Progress(((100 * it.bytesTransferred) / it.totalByteCount).toString()))
        }
        try {
            file.putFile(uri).addOnProgressListener(listener).await()
            val snapshot = firebaseDatabase.getReference("Users").child(firebaseAuth.currentUser!!.uid).get().await()
            val user = snapshot.getValue(UserShort::class.java)
            val ref = firebaseDatabase.getReference("Classes/$className").orderByChild("title").equalTo(issueName).get().await()
            var path = ref.ref.path.toString()
            ref.children.forEach {
                path = it.ref.path.wireFormat()
            }

            val submit = Submit(path, user!!.uid, user.name, user.imageURL,
                    Functions.getCurrentDate().toString(), fileName, file.downloadUrl.await().toString())
            firebaseDatabase.getReference(path).child("submits").child(user.uid).setValue(submit).await()
            //adding activity
            val submission = ActivityItem(className, issueName, fileName, Functions.getCurrentDate().toString(), "submission")
            firebaseDatabase.getReference("Users/${user.uid}/activities").push().setValue(submission).await()
            //
            offer(DataState.Success(file.downloadUrl.await().toString()))
        } catch (cause: EssError) {
            offer(DataState.Error(cause))
        }
        awaitClose { file.putFile(uri).removeOnProgressListener(listener) }
    }


}

interface StudentRepoImpl {
    fun submitToIssue(uri: Uri, className: String, issueName: String,
                      fileName: String): Flow<DataState<String>>

}