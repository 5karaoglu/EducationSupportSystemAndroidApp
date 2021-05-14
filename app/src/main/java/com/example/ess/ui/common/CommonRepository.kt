package com.example.ess.ui.common

import android.annotation.SuppressLint
import android.util.Log
import com.example.ess.model.*
import com.example.ess.util.DataState
import com.example.ess.util.EssError
import com.example.ess.util.Functions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.sql.Timestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class CommonRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private var firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
): RepoHelper{


    @ExperimentalCoroutinesApi
    override suspend fun getSubmissions(feedItem: FeedItem): Flow<DataState<List<Submit>>> = callbackFlow {
        val list = mutableListOf<Submit>()
        val snapshot = firebaseDatabase.getReference(feedItem.path)
                .child("submits")
        offer(DataState.Loading)
        val listener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Submit::class.java)?.let { list.add(it) }
                this@callbackFlow.offer(DataState.Success(list))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.offer(DataState.Error(Throwable(error.message)))
            }
        }
        snapshot.addChildEventListener(listener)
        awaitClose{snapshot.removeEventListener(listener)}

    }

    @ExperimentalCoroutinesApi
    override suspend fun getNotifications(): Flow<DataState<List<Notification>>> = callbackFlow {
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
        val cUser = ss.getValue(User::class.java)
        val classList = mutableListOf<String>()
        val list = mutableListOf<Notification>()
        val listener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Notification::class.java)?.let { list.add(it) }
                this@callbackFlow.offer(DataState.Success(list))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.offer(DataState.Error(Throwable(error.message)))
            }
        }
        val snapshot = firebaseDatabase.getReference("Users/${cUser!!.uid}/Classes").get().await()
        snapshot.children.forEach {
            firebaseDatabase.getReference("Notifications/${it.key}").addChildEventListener(listener)
        }

        awaitClose{
            snapshot.children.forEach {
                firebaseDatabase.getReference("Notifications/${it.key}").removeEventListener(listener)
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun downloadFile(submit: Submit) = callbackFlow<DataState<String>> {
        offer(DataState.Loading)
        val progressListener = OnProgressListener<FileDownloadTask.TaskSnapshot> {
            this.offer(DataState.Progress(((100*it.bytesTransferred)/it.totalByteCount).toString()))
        }
        val successListener = OnSuccessListener<FileDownloadTask.TaskSnapshot>{
            this.offer(DataState.Success("File downloaded successfully!"))
        }
        val errorListener = OnFailureListener{
            this.offer(DataState.Error(it))
        }
        val local = File.createTempFile(submit.name,".pdf")
        val ref = firebaseStorage.getReferenceFromUrl(submit.downloadUrl).getFile(local)
       ref.addOnProgressListener(progressListener).addOnSuccessListener(successListener)
               .addOnFailureListener(errorListener)
        awaitClose { ref.removeOnProgressListener(progressListener).removeOnSuccessListener(successListener)
                .removeOnFailureListener(errorListener)}
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

    suspend fun getUser(uid:String): Flow<DataState<User>> = flow {
        emit(DataState.Loading)
        try {
            val snapshot = firebaseDatabase.getReference("Users/$uid").get().await()
            emit(DataState.Success(
                UserMapper.mapToModel(
                    snapshot.value as HashMap<String, *>
                )
            ))
        }catch (cause:EssError){
            emit(DataState.Error(cause))
        }
    }

    /*@ExperimentalCoroutinesApi
    suspend fun getMessages(chatID: String): Flow<DataState<Message>> = callbackFlow {
        offer(DataState.Loading)
            val snapshot = firebaseDatabase.getReference("Contacts/$chatID").addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = MessageMapper.mapToModel(snapshot.value as HashMap<String, *>)
                    offer(DataState.Success(message))
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onChildRemoved(snapshot: DataSnapshot) {
                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        awaitClose { }
    }*/

    @ExperimentalCoroutinesApi
    override fun getMessages(chatID: String) = callbackFlow<DataState<List<Message>>> {
        val list = mutableListOf<Message>()
        val listener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                this@callbackFlow.offer(DataState.Loading)
                Log.d("debug", "onChildAdded0: childadded")
                list.add(MessageMapper.mapToModel(snapshot.value as HashMap<String, *>))

                this@callbackFlow.offer(DataState.Success(list))

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("debug", "onChildAdded: childchanged")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("debug", "onChildAdded: childremoved")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("debug", "onChildAdded: childmoved")
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.offer(DataState.Error(Throwable(error.message)))
            }
        }
        firebaseDatabase.getReference("contacts/$chatID").addChildEventListener(listener)
        Log.d("debug", "getMessages: here")

        awaitClose { firebaseDatabase.getReference("contacts/$chatID").removeEventListener(listener)
            Log.d("debug", "getMessages: removed")}
    }
    /*suspend fun updateLastMessage(contact: Contact,lastMessage:Message) = withContext(Dispatchers.IO){
        val myKey = firebaseDatabase.getReference("Users/${contact.myUid}/contacts")..orderByChild("chat_id").equalTo(contact.chatId).get().await()
        firebaseDatabase.getReference("Users/${contact.myUid}/contacts/${myKey.value}").child("last_message").setValue(lastMessage.message)
        val receiverKey = firebaseDatabase.getReference("Users/${contact.receiverUid}/contacts").orderByChild("chat_id").equalTo(contact.chatId).get().await()
        firebaseDatabase.getReference("Users/${contact.receiverUid}/contacts/${receiverKey.chvalue}").child("last_message").setValue(lastMessage.message)
    }*/


    suspend fun getUsers(query: String): Flow<DataState<List<UserShort>>> = flow {
        emit(DataState.Loading)
        try {
            val list = mutableListOf<UserShort>()
            val users = firebaseDatabase.getReference("Users").orderByChild("email").startAt(query).get().await()
            users.children.forEach {
                list.add(UserShortMapper.mapToModel(it.value as HashMap<String, *>))
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

    suspend fun getContacts():Flow<DataState<List<Contact>>> = flow {
        emit(DataState.Loading)
        try {
            val list = mutableListOf<Contact>()
            val snapshot = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/contacts").get().await()
            snapshot.children.forEach {
                list.add(ContactMapper.mapToModel(
                        it.value as HashMap<String, *>
                ))
            }
            emit(DataState.Success(list))
        }catch (cause:EssError){
            emit(DataState.Error(cause))
        }
    }
    /*suspend fun getContact(receiverUid: String) = withContext(Dispatchers.IO) {

        var contact: Contact? = null
        val snapshot = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/contacts").orderByChild("receiver_uid").equalTo(receiverUid).get().await()
        snapshot.children.forEach {
            contact = ContactMapper.mapToModel(it.value as HashMap<String, *>)
        }
        if (contact == null){
            createContact(receiverUid)
        }
         return@withContext  contact
    }*/


    suspend fun getContact(receiverUid:String) = withContext(Dispatchers.IO){
        var user: User? = null
        var contact: Contact? = null
        val snapshot = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/contacts")
                .orderByChild("receiver_uid").equalTo(receiverUid).get().await()
        snapshot.children.forEach {
            contact = ContactMapper.mapToModel(it.value as HashMap<String, *>)
        }
        if (contact == null){
            val receiver = firebaseDatabase.getReference("Users/$receiverUid").get().await()
            user = UserMapper.mapToModel(receiver.value as HashMap<String, *>)

            val chatID = firebaseDatabase.getReference("contacts").push()
            //setting data for message sender
            firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/contacts").push().updateChildren(
                    ContactMapper.modelToMap(
                            Contact(
                                    chatId = chatID.key!!,
                                    imageUrl = user!!.imageUrl!!,
                                    lastMessage = "",
                                    myUid = firebaseAuth.currentUser!!.uid,
                                    name = user!!.name!!,
                                    receiverUid = user!!.uid!!
                            )
                    )
            )
            //setting data for message receiver
            firebaseDatabase.getReference("Users/$receiverUid/contacts").push().updateChildren(
                    ContactMapper.modelToMap(
                            Contact(
                                    chatId = chatID.key!!,
                                    imageUrl = firebaseAuth.currentUser!!.photoUrl.toString(),
                                    lastMessage = "",
                                    myUid = user!!.uid!!,
                                    name = firebaseAuth.currentUser!!.displayName!!,
                                    receiverUid = firebaseAuth.currentUser!!.uid
                            )
                    )
            )
            contact = Contact(chatId = chatID.key!!, imageUrl = user!!.imageUrl!!,
                    lastMessage = "", myUid = firebaseAuth.currentUser!!.uid,
                    name = user!!.name!!, receiverUid = user!!.uid!!)
            return@withContext contact
        }else{
            return@withContext contact
        }
    }

    suspend fun sendMessage(chatID: String,message: String){
        firebaseDatabase.getReference("contacts/$chatID").push().updateChildren(
                MessageMapper.modelToMap(
                        Message(
                                timestamp = Functions.getCurrentDate().toString(),
                                message = message,
                                whoSent = firebaseAuth.currentUser!!.uid
                        )
                )
        ).await()
    }

    @ExperimentalCoroutinesApi
    override suspend fun getComments(feedItem: FeedItem): Flow<DataState<List<Comment>>> = callbackFlow {
        offer(DataState.Loading)
        val list = mutableListOf<Comment>()
        val snapshot = firebaseDatabase.getReference(feedItem.path).child("comments")
        val listener = object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Comment::class.java)?.let { list.add(it) }
                list.reverse()
                this@callbackFlow.offer(DataState.Success(list))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
               this@callbackFlow.offer(DataState.Error(Throwable(error.message)))
            }
        }

        snapshot.addChildEventListener(listener)
        awaitClose { snapshot.removeEventListener(listener) }

    }

    @SuppressLint("RestrictedApi")
    suspend fun addComment(feedItem: FeedItem, comment:String) = withContext(Dispatchers.IO){
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
        val user = ss.getValue(UserProfile::class.java)
        val ss2 = firebaseDatabase.getReference(feedItem.path).child("comments").push()
        val commentItem = Comment(ss2.ref.path.wireFormat(),comment,
                user!!.imageUrl,user.name,user.uid,Functions.getCurrentDate().toString())
        ss2.setValue(commentItem).await()
        //adding activity
        val activityItem = ActivityItem(feedItem.className,feedItem.title,comment,Functions.getCurrentDate().toString(),"comment")
        firebaseDatabase.getReference("Users/${user.uid}").child("activities").push().setValue(activityItem)
    }

}
interface RepoHelper{
    fun getMessages(chatID: String): Flow<DataState<List<Message>>>
    fun downloadFile(submit: Submit): Flow<DataState<String>>
    suspend fun getComments(feedItem: FeedItem): Flow<DataState<List<Comment>>>
    suspend fun getSubmissions(feedItem: FeedItem): Flow<DataState<List<Submit>>>
    suspend fun getNotifications(): Flow<DataState<List<Notification>>>
}