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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
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

    override suspend fun subscribeToChannel(className: String): Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        try {
            firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").child("Classes")
                .child(className).setValue(true).await()
            emit(DataState.Success(className))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

    override suspend fun getClassList(): Flow<DataState<List<String>>> = flow{
       emit(DataState.Loading)
        try {
            val uid = firebaseAuth.currentUser!!.uid
            val list = mutableListOf<String>()
            val snapshot = firebaseDatabase.getReference("Users/$uid/Classes").orderByKey().get().await()
            snapshot.children.forEach {
                it.key?.let { it1 -> list.add(it1) }
            }
            emit(DataState.Success(list))
        }catch (cause: EssError){
            emit(DataState.Error(cause))
        }
    }

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
        val list = mutableListOf<Notification>()
        val listener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Notification::class.java)?.let { list.add(it) }
                val sorted = list.sortedWith(compareBy { it.timestamp })
                this@callbackFlow.offer(DataState.Success(sorted))
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
    override suspend fun getFriendRequests(): Flow<DataState<List<User>>> = callbackFlow {
        val list = mutableListOf<User>()
        val listener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(User::class.java)?.let { list.add(it) }
                this@callbackFlow.offer(DataState.Success(list))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {
                list.remove(snapshot.getValue(User::class.java))
                this@callbackFlow.offer(list)
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
               this@callbackFlow.offer(DataState.Error(Throwable(error.message)))
            } }
        offer(DataState.Loading)
        val snapshot = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/friend_requests")
        snapshot.addChildEventListener(listener)

        awaitClose { snapshot.removeEventListener(listener) }
    }

    override suspend fun addFriend(user: User): Unit = withContext(Dispatchers.IO) {
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
        val cUser = ss.getValue(User::class.java)
        //adding friends to users node
       firebaseDatabase.getReference("Users/${cUser!!.uid}")
           .child("friends").child(user.uid!!).setValue(user)
        //adding friends to cUsers node
        firebaseDatabase.getReference("Users/${user.uid}")
            .child("friends").child(cUser.uid!!).setValue(cUser)
        firebaseDatabase.getReference("Users/${cUser.uid}").child("friend_requests")
            .child(user.uid)
            .removeValue()
    }

    override suspend fun denyFriendRequest(user: User) {
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
        val cUser = ss.getValue(User::class.java)
        firebaseDatabase.getReference("Users/${cUser!!.uid}").child("friend_requests")
            .child(user.uid!!)
            .removeValue()
    }

    override suspend fun sendFriendRequest(user: UserProfile) {
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
        val cUser = ss.getValue(User::class.java)
        firebaseDatabase.getReference("Users/${user.uid}/friend_requests").child(cUser!!.uid!!).setValue(cUser)
    }

    override suspend fun isFriend(user: UserProfile): Boolean {
        var sUser : UserProfile? = null
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/friends")
            .orderByKey().equalTo(user.uid).get().await()
        ss.children.forEach {
            sUser = it.getValue(UserProfile::class.java)
        }
        return sUser != null
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
                val currentClass = firebaseDatabase.getReference("Classes/${it.key}").orderByChild("deadline").get().await()
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
                val sorted = feedList.sortedWith(compareBy { it.deadline })
                emit(DataState.Success(sorted))

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


    suspend fun getUsers(query: String): Flow<DataState<List<User>>> = flow {
        emit(DataState.Loading)
        try {
            val list = mutableListOf<User>()
            val users = firebaseDatabase.getReference("Users").orderByChild("email").startAt(query).get().await()
            users.children.forEach {
                it.getValue(User::class.java)?.let { it1 -> list.add(it1) }
            }
            if (list.isNotEmpty()){
                emit(DataState.Success(list))
            }else{
                emit(DataState.Empty)
            }
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


    suspend fun getContact(receiverUid:String) = withContext(Dispatchers.IO){
        var receiver: UserProfile? = null
        var cUser: UserProfile? = null
        var contact: Contact? = null
        val snapshot = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/contacts")
                .orderByChild("receiver_uid").equalTo(receiverUid).get().await()
        snapshot.children.forEach {
            contact = it.getValue(Contact::class.java)
        }
        if (contact == null){
            val ss = firebaseDatabase.getReference("Users/$receiverUid").get().await()
            receiver = ss.getValue(UserProfile::class.java)
            val ss2 = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
            cUser = ss.getValue(UserProfile::class.java)

            val chatID = firebaseDatabase.getReference("contacts").push()
            //setting data for message sender
            firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}/contacts").push().updateChildren(
                    ContactMapper.modelToMap(
                            Contact(
                                    chatId = chatID.key!!,
                                    imageUrl = receiver!!.imageUrl!!,
                                    lastMessage = "Chat started",
                                    myUid = cUser!!.uid,
                                    name = receiver!!.name!!,
                                    receiverUid = receiver!!.uid!!
                            )
                    )
            )
            //setting data for message receiver
            firebaseDatabase.getReference("Users/${receiver.uid}/contacts").push().updateChildren(
                    ContactMapper.modelToMap(
                            Contact(
                                    chatId = chatID.key!!,
                                    imageUrl = cUser.imageUrl,
                                    lastMessage = "Chat started",
                                    myUid = receiver.uid!!,
                                    name = cUser.name,
                                    receiverUid = cUser.uid
                            )
                    )
            )
            contact = Contact(chatId = chatID.key!!, imageUrl = receiver!!.imageUrl!!,
                    lastMessage = "", myUid = cUser.uid,
                    name = receiver!!.name!!, receiverUid = receiver!!.uid!!)
            return@withContext contact
        }else{
            return@withContext contact
        }
    }

    suspend fun getUserProfile() = withContext(Dispatchers.IO){
        val ss = firebaseDatabase.getReference("Users/${firebaseAuth.currentUser!!.uid}").get().await()
        val user = ss.getValue(UserProfile::class.java)
        user!!.classesCount = ss.child("Classes").childrenCount.toString()
        user.friendsCount = ss.child("friends").childrenCount.toString()
        Log.d("debug", "getUserInfo: ${user.imageUrl}")
        return@withContext user
    }
    suspend fun getUserProfile(uid: String) = withContext(Dispatchers.IO){
        val ss = firebaseDatabase.getReference("Users/$uid").get().await()
        Log.d("TAG", "getUserProfile: $ss")
        val cUser = ss.getValue(UserProfile::class.java)
        Log.d("TAG", "getUserProfile: $cUser")
        cUser!!.classesCount = ss.child("Classes").childrenCount.toString()
        cUser.friendsCount = ss.child("friends").childrenCount.toString()
        return@withContext cUser
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
        val snapshot = firebaseDatabase.getReference(feedItem.path).child("comments").orderByChild("timestamp")
        val listener = object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Comment::class.java)?.let { list.add(it) }
                val sorted = list.sortedWith(compareBy { it.timestamp })
                this@callbackFlow.offer(DataState.Success(sorted))
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

    override suspend fun getActivities(user: UserProfile) : Flow<DataState<List<ActivityItem>>> = flow {
        emit(DataState.Loading)
        val list = mutableListOf<ActivityItem>()
        try {
            val snapshot = firebaseDatabase.getReference("Users/${user.uid}/activities").get().await()
            snapshot.children.forEach {
                it.getValue(ActivityItem::class.java)?.let { it1 -> list.add(it1)
                    Log.d("TAG", "getActivities: $it1")}
            }
            if (list.isNotEmpty()){
                list.reverse()
                emit(DataState.Success(list))
            }else{
                emit(DataState.Empty)
            }
        }catch (cause:EssError){
            emit(DataState.Error(cause))
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun dummyData() = withContext(Dispatchers.IO){
        ///teachers
        val pcprincipal = UserDummy("bla7jvAqTSVhTtantJJHNtQnrIy2","PC Principal","pcprincipal@sp.com",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKXI5FqJb6a7cNDQqq7qVqAGjG0hKR_Pv-dTgCKPA2BsztN4j8IfQOO4nctLVMnRGWD2w&usqp=CAU","Teacher")
        val mackey = UserDummy("FGmbgqJLC4VO8WLRJtYKTlmKryZ2","Mr. Mackey","mackey@sp.com",
                "https://static.wikia.nocookie.net/southpark/images/5/58/Mr_Mackey_New.png/revision/latest/scale-to-width-down/225?cb=20160402100931","Teacher")
        val catherine = UserDummy("pjwHixMOvsWlEjE0STihNE7VFNO2","Catherine","sm@sp.com",
                "https://static.wikia.nocookie.net/southpark/images/9/96/School-faculty-strong-woman.png/revision/latest/scale-to-width-down/168?cb=20171130193505","Teacher")


        val ericcartman = UserDummy("NWAzaig6FVhLuT6wM4ho4urkpc22","Eric Cartman","eric@sp.com",
                "https://static.wikia.nocookie.net/southpark/images/c/c4/Eric-cartman.png/revision/latest/scale-to-width-down/310?cb=20180718082504","Student")
        val stanmarsh = UserDummy("Ls4ySnPsRCXK5jCPHrnE4ZbebTB2","Stan Marsh","stan@sp.com",
                "https://static.wikia.nocookie.net/southpark/images/c/c6/Stan-marsh-0.png/revision/latest/scale-to-width-down/310?cb=20210107202918","Student")
        val ike = UserDummy("","Ike Broflowski","",
                "https://static.wikia.nocookie.net/southpark/images/a/af/Ike-current.png/revision/latest?cb=20180521124521")
        val kenny = UserDummy("","Kenny Mccormick","",
        "https://static.wikia.nocookie.net/southpark/images/6/6f/KennyMcCormick.png/revision/latest/scale-to-width-down/310?cb=20160409020502")
        val wendy = UserDummy("","Wendy Testaburger","",
        "https://static.wikia.nocookie.net/southpark/images/9/9e/Wendyy.png/revision/latest/scale-to-width-down/310?cb=20171014061739")
        val shelly = UserDummy("","Shelly Marsh","",
        "https://static.wikia.nocookie.net/southpark/images/3/3b/Shelly.png/revision/latest/scale-to-width-down/310?cb=20160406113409")
        val randy = UserDummy("","Randy Marsh","",
        "https://static.wikia.nocookie.net/southpark/images/e/e2/Farmer-randy.png/revision/latest/scale-to-width-down/255?cb=20181021111756")
        val nathan = UserDummy("","Nathan","",
        "https://static.wikia.nocookie.net/southpark/images/c/cb/Nathan.png/revision/latest/scale-to-width-down/310?cb=20160408235818")
        val heidi = UserDummy("","Heidi Turner","",
        "https://static.wikia.nocookie.net/southpark/images/b/b8/HeidiTurnerHat.png/revision/latest/scale-to-width-down/310?cb=20171128215241")
        val nichole = UserDummy("","Nichole Daniels","",
        "https://static.wikia.nocookie.net/southpark/images/e/e4/Nichole_2.png/revision/latest/scale-to-width-down/310?cb=20170906201058")
        val list2= mutableListOf(ike,kenny,wendy,shelly,randy,nathan,heidi,nichole)
        /*val list = mutableListOf<UserDummy>(pcprincipal,mackey,catherine,ericcartman,stanmarsh)
        val list2= mutableListOf(ike,kenny,wendy,shelly,randy,nathan,heidi,nichole)
        list.forEach {
            list.forEach { inner ->
                if (inner.uid != it.uid){
                    firebaseDatabase.getReference("Users/${it.uid}").child("friends").child(inner.uid!!).setValue(inner)
                }
            }
            list2.forEach { list2 ->
                firebaseDatabase.getReference("Users/${it.uid}").child("friends").push().setValue(list2)
            }
        }*/
        /*firebaseDatabase.getReference("Users/${catherine.uid}").setValue(catherine)
        firebaseDatabase.getReference("Users/${ericcartman.uid}").setValue(ericcartman)
        firebaseDatabase.getReference("Users/${stanmarsh.uid}").child("friends").child(mackey.uid!!).setValue(mackey)*/

        /*val feed = FeedItem("","HomeEconomics","HE Homework 1",
                "Help your parents cook and write down your observations.","","",
                "Catherine","pjwHixMOvsWlEjE0STihNE7VFNO2",
                "https://static.wikia.nocookie.net/southpark/images/9/96/School-faculty-strong-woman.png/revision/latest/scale-to-width-down/168?cb=20171130193505",
                "1622937599000","","")
        val feed2 = FeedItem("","HomeEconomics","HE Homework 2",
                "Find out what bills are coming to your home and write down your thoughts.","","",
                "Catherine","pjwHixMOvsWlEjE0STihNE7VFNO2",
                "https://static.wikia.nocookie.net/southpark/images/9/96/School-faculty-strong-woman.png/revision/latest/scale-to-width-down/168?cb=20171130193505",
                "1623369599000","","")
        val feed3 = FeedItem("","HomeEconomics","HE Homework 3",
                "Find out what bills are coming to your home and write down your thoughts.","none","test1.pdf",
                "Catherine","pjwHixMOvsWlEjE0STihNE7VFNO2",
                "https://static.wikia.nocookie.net/southpark/images/9/96/School-faculty-strong-woman.png/revision/latest/scale-to-width-down/168?cb=20171130193505",
                "1623542399000","","")*/
        /*val feed = FeedItem("","ComputerLab","Browsers",
                "Write down 5 web browser names and why we need browsers m'kay?","","",
                "Mr. Mackey","FGmbgqJLC4VO8WLRJtYKTlmKryZ2",
                "https://static.wikia.nocookie.net/southpark/images/5/58/Mr_Mackey_New.png/revision/latest/scale-to-width-down/225?cb=20160402100931",
                "1622937599000","","")
        val feed2 = FeedItem("","ComputerLab","Download a photo",
                "Download a photo from internet and upload it here m'kay?","","",
                "Mr. Mackey","FGmbgqJLC4VO8WLRJtYKTlmKryZ2",
                "https://static.wikia.nocookie.net/southpark/images/5/58/Mr_Mackey_New.png/revision/latest/scale-to-width-down/225?cb=20160402100931",
                "1622937599500","","")
        val feed3 = FeedItem("","ComputerLab","Drawing",
                "Draw something on paint and upload it here m'kay?","","",
                "Mr. Mackey","FGmbgqJLC4VO8WLRJtYKTlmKryZ2",
                "https://static.wikia.nocookie.net/southpark/images/5/58/Mr_Mackey_New.png/revision/latest/scale-to-width-down/225?cb=20160402100931",
                "1622937499000","","")*/
        val feed = FeedItem("","Math","4 operations",
                "Write down 10 examples for each operation.","","",
                "PC Principal","bla7jvAqTSVhTtantJJHNtQnrIy2",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKXI5FqJb6a7cNDQqq7qVqAGjG0hKR_Pv-dTgCKPA2BsztN4j8IfQOO4nctLVMnRGWD2w&usqp=CAU",
                "1622937519000","","")
        val feed2 = FeedItem("","Math","4 operations",
                "Solve attached problems.","","",
                "PC Principal","bla7jvAqTSVhTtantJJHNtQnrIy2",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKXI5FqJb6a7cNDQqq7qVqAGjG0hKR_Pv-dTgCKPA2BsztN4j8IfQOO4nctLVMnRGWD2w&usqp=CAU",
                "1622937519000","","")
        val feed3 = FeedItem("","Math","4 operations",
                "Write down 10 examples for each operation","","",
                "PC Principal","bla7jvAqTSVhTtantJJHNtQnrIy2",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKXI5FqJb6a7cNDQqq7qVqAGjG0hKR_Pv-dTgCKPA2BsztN4j8IfQOO4nctLVMnRGWD2w&usqp=CAU",
                "1622937519000","","")
        val list = mutableListOf(feed,feed2,feed3)
        val feedlist = mutableListOf<FeedItem>()
        val comments = listOf("Wow ! That random comments are awesome :)","What am i doing ? Oh yeah","I guess that'll be rare.","What is meaning all of this ?",
        "Now I'm sure i have some questions to ask","That riff tho...","You should listen Do I Wanna Know definitely.","Meet me on discord at 12.30 am",
        "You forgot to call your girlfriend. Oh wait, you don't have one :(","Let's do some mining on Minecraft.","You have to invest bitcoin.","M'kay?",
        "Follow my hottest playlist on TIDAL.","I realised i have to add more string.","At least this is fun","But coding doesn't.",
        "When we have icecreams together ?","You should some treats yourself.","Can i be your daily covid vaccine ?","Maybe this comment should be about class.",
        "As the wiseman said 'Ra ra rasputin, lover of the russian queen'")
        /*val ss = firebaseDatabase.getReference("Classes/HomeEconomics").orderByKey().get().await()
        ss.children.forEach {
            it.getValue(FeedItem::class.java)?.let { it1 -> feedlist.add(it1) }
        }
        feedlist.forEach { feedi ->
            list2.forEach {
                val snap = firebaseDatabase.getReference(feedi.path).child("comments").push()
                *//*val submit = Submit(snap.ref.path.wireFormat(),it.uid!!, it.name!!,it.imageUrl,Functions.getCurrentDate().toString(),"hw.pdf","none")
                snap.setValue(submit).await()*//*
                val comm = Comment(snap.ref.path.wireFormat(),comments.random(),it.imageUrl,it.name!!,it.uid!!,Functions.getCurrentDate().toString())
                snap.setValue(comm)
            }
        }*/
    }

}
interface RepoHelper{
    fun getMessages(chatID: String): Flow<DataState<List<Message>>>
    fun downloadFile(submit: Submit): Flow<DataState<String>>
    suspend fun getComments(feedItem: FeedItem): Flow<DataState<List<Comment>>>
    suspend fun getSubmissions(feedItem: FeedItem): Flow<DataState<List<Submit>>>
    suspend fun getNotifications(): Flow<DataState<List<Notification>>>
    suspend fun getFriendRequests(): Flow<DataState<List<User>>>
    suspend fun addFriend(user: User)
    suspend fun denyFriendRequest(user: User)
    suspend fun sendFriendRequest(user: UserProfile)
    suspend fun isFriend(user: UserProfile):Boolean
    suspend fun getActivities(user: UserProfile): Flow<DataState<List<ActivityItem>>>
    suspend fun getClassList(): Flow<DataState<List<String>>>
    suspend fun subscribeToChannel(className: String): Flow<DataState<String>>
}