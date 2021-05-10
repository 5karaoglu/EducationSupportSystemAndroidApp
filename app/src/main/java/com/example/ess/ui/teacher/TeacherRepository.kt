package com.example.ess.ui.teacher

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.ess.model.*
import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.example.ess.util.Functions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private var firebaseDatabase: FirebaseDatabase,
    private var firebaseStorage: FirebaseStorage,
    private val firebaseMessaging: FirebaseMessaging
){
    private val TAG = "Teacher Repository"

    suspend fun getComments(feedItem: FeedItem): Flow<DataState<List<Comment>>> = flow {
        emit(DataState.Loading)
        val list = mutableListOf<Comment>()
        try {
            val snapshot = firebaseDatabase.getReference(feedItem.path)
                    .child("comments").get().await()
            snapshot.children.forEach{
                it.getValue(Comment::class.java)?.let { comment ->
                    comment.subCommentsCount = it.child("sub_comments").childrenCount.toString()
                    list.add(comment)
                }
            }
            Log.d(TAG, "getComments: $snapshot")

            emit(DataState.Success(list))
        } catch (cause: EssError) {
            emit(DataState.Error(cause))
        }
    }

    suspend fun getSubmits(feedItem: FeedItem): Flow<DataState<List<Submit>>> = flow {
        emit(DataState.Loading)
        val list = mutableListOf<Submit>()
        try {
            Log.d(TAG, "getSubmits: ${feedItem.path}")
            val snapshot = firebaseDatabase.getReference(feedItem.path)
                    .child("submits").get().await()
            Log.d(TAG, "getSubmits: $snapshot")
            snapshot.children.forEach{
                it.getValue(Submit::class.java)?.let { submit ->
                    list.add(submit)
                }
            }

            emit(DataState.Success(list))
        } catch (cause: EssError) {
            emit(DataState.Error(cause))
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun getFeed(): Flow<DataState<List<FeedItem>>> = flow {
        emit(DataState.Loading)
        val feedList = mutableListOf<FeedItem>()
        try {
            val snapshot = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}")
                    .child("Classes").get().await()
            snapshot.children.forEach { it ->
                val currentClass = firebaseDatabase.getReference("Classes/${it.key}").get().await()
                currentClass.children.forEach { feed ->
                    val item = feed.getValue(FeedItem::class.java)
                    item?.let { cItem ->
                        cItem.path = feed.ref.path.toString()
                        cItem.commentsCount = feed.child("comments").childrenCount.toString()
                        cItem.submitsCount = feed.child("submits").childrenCount.toString()
                    }
                    item?.let { it1 -> feedList.add(it1) }
                }
            }
            if (feedList.isNotEmpty()){
                emit(DataState.Success(feedList))

            }else{
                Log.d("debug", "getFeed:Listempty")
            }
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun getIssueKey(selectedClass:String) = withContext(Dispatchers.IO){
        val push = firebaseDatabase.getReference("Classes").child(selectedClass).push()
        push.child("path").setValue(push.ref.path.toString()).await()
        return@withContext push.key
    }
    @SuppressLint("RestrictedApi")
    suspend fun createIssue(className:String,title:String,description:String,
                            deadline:String,fileName: String?,downloadUrl:String?)
            : Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        try {
            val cUser = firebaseAuth.currentUser
            val push = firebaseDatabase.getReference("Classes").child(className).push()
            push.child("path").setValue(push.ref.path.toString()).await()
            firebaseDatabase.getReference("Classes/$className").child(push.key!!)
                .updateChildren(IssueShortMapper.modelToMap(
                    IssueShort(
                        title = title,
                        description = description,
                        deadline = deadline,
                        publishedBy = cUser!!.displayName!!,
                        fileName = fileName,
                        downloadUrl = downloadUrl
                    )
                )).await()
            emit(DataState.Success("Issue updated successfully !"))
        }catch (cause:EssError){
            emit(DataState.Error(cause))
        }
    }

    suspend fun updateIssue(className:String,title:String,description:String,
                            deadline:String,fileName: String?,downloadUrl:String?)
    : Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        try {
            val cUser = firebaseAuth.currentUser
            var key: String? = null
            val snapshot = firebaseDatabase.getReference("Classes/$className").orderByChild("title").equalTo(title).get().await()
            snapshot.children.forEach {
                key = it.key
            }
            firebaseDatabase.getReference("Classes/$className/$key")
                .updateChildren(IssueShortMapper.modelToMap(
                    IssueShort(
                        title = title,
                        description = description,
                        deadline = deadline,
                        publishedBy = cUser!!.displayName!!,
                        fileName = fileName,
                        downloadUrl = downloadUrl
                    )
                )).await()
            emit(DataState.Success("Issue updated successfully !"))
        }catch (cause:EssError){
            emit(DataState.Error(cause))
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun uploadFile(uri: Uri, className: String, issue: String, fileName:String):Flow<DataState<String>> = callbackFlow{
        sendBlocking(DataState.Loading)
        val file =firebaseStorage.getReference("Classes/$className/$issue/$fileName")
        try {
            file.putFile(uri).addOnProgressListener {
                sendBlocking(DataState.Progress(((100*it.bytesTransferred)/it.totalByteCount).toString()))
            }.await()
            sendBlocking(DataState.Success(file.downloadUrl.await().toString()))
        }catch (cause:EssError){
            sendBlocking(DataState.Error(cause))
        }
        awaitClose { }
    }

    suspend fun getIssue(className: String, issue:String) = withContext(Dispatchers.IO){
        val snapshot = firebaseDatabase.getReference("Classes/$className").orderByChild("title").equalTo(issue).get().await()
        var issueShort: IssueShort? = null
        snapshot.children.forEach {
           issueShort = IssueShortMapper.mapToModel(it.value as HashMap<String, *>)
            Log.d("debug", "getIssue: ${it.child("title").value}")
        }
        return@withContext issueShort

    }

    suspend fun getClassList() = withContext(Dispatchers.IO){
        val uid = firebaseAuth.currentUser!!.uid
        val list = mutableListOf<String>()
        val snapshot = firebaseDatabase.getReference("Users/$uid/Classes").orderByKey().get().await()
        snapshot.children.forEach {
            it.key?.let { it1 -> list.add(it1) }
        }
        return@withContext list
    }

    suspend fun getIssueList(className: String) = withContext(Dispatchers.IO){
        val list = mutableListOf<String>()
        val snapshot = firebaseDatabase.getReference("Classes/$className").orderByChild("title").get().await()
        snapshot.children.forEach {
            list.add(it.child("title").value as String)
        }
        return@withContext list
    }

   /* suspend fun getIssueList(className: String) = withContext(Dispatchers.IO){
        val uid = firebaseAuth.currentUser!!.uid
        val list = mutableListOf<IssueShort>()
        val snapshot = firebaseDatabase.getReference("Classes/$className").orderByChild("title").get().await()
        snapshot.children.forEach {
           list.add( IssueShortMapper.mapToModel(it.value as HashMap<String, *>))
        }
        return@withContext list
    }*/

    suspend fun uploadPhoto(uri: Uri?) = withContext(Dispatchers.IO){
       if (uri != null){
           val file = firebaseStorage.getReference("Users").child("${firebaseAuth.currentUser!!.uid}/images")
           file.putFile(uri).await()
           return@withContext file.downloadUrl.await()
       }else return@withContext null
    }

    suspend fun getUserInfo() = withContext(Dispatchers.IO){
        val user = firebaseAuth.currentUser
        Log.d("debug", "getUserInfo: ${user!!.photoUrl}")
        return@withContext UserMapper.dbToUser(user!!)

    }
    suspend fun updateUser(user: User): Flow<DataState<String>> = flow {
        Log.d("debug", "updateUser: ${user.imageURL}, ${user.name}, ${user.email}")
        emit(DataState.Loading)
        try {
            val cUser = firebaseAuth.currentUser!!
            cUser.updateProfile(UserProfileChangeRequest.Builder()
                    .setPhotoUri(user.imageURL!!.toUri())
                    .build())
            firebaseDatabase.getReference("Users/${cUser.uid}/imageURL").setValue(user.imageURL)
            cUser.updateProfile(UserProfileChangeRequest.Builder()
                    .setDisplayName(user.name)
                    .build())
            firebaseDatabase.getReference("Users/${cUser.uid}/name").setValue(user.name)
            user.email?.let { cUser.updateEmail(it) }
            firebaseDatabase.getReference("Users/${cUser.uid}/email").setValue(user.email)
            emit(DataState.Success("User updated successfully !"))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

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
            list.reverse()
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
            firebaseDatabase.getReference("Users/${user?.uid}/SubscribedChannels/$channelName").setValue(true).await()
    }
}