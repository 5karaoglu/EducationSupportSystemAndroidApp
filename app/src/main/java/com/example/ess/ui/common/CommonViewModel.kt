package com.example.ess.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.model.*
import com.example.ess.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonViewModel
    @Inject constructor(private val repository: CommonRepository): ViewModel() {

    private var _userState: MutableLiveData<DataState<List<User>>> = MutableLiveData()
    val userState : LiveData<DataState<List<User>>>
        get() = _userState

    private var _contactState: MutableLiveData<DataState<List<Contact>>> = MutableLiveData()
    val contactState : LiveData<DataState<List<Contact>>>
        get() = _contactState

    private var _messagesState: MutableLiveData<DataState<List<Message>>> = MutableLiveData()
    val messagesState : LiveData<DataState<List<Message>>>
        get() = _messagesState

    private var _userProfileState: MutableLiveData<DataState<User>> = MutableLiveData()
    val userProfileState : LiveData<DataState<User>>
        get() = _userProfileState

    private var _chatId: MutableLiveData<String> = MutableLiveData()
    val chatId : LiveData<String>
        get() = _chatId

    private var _contact: MutableLiveData<Contact> = MutableLiveData()
    val contact : LiveData<Contact>
        get() = _contact

    private val _feedState : MutableLiveData<DataState<List<FeedItem>>> = MutableLiveData()
    val feedState : LiveData<DataState<List<FeedItem>>>
        get() = _feedState

    private val _submitsState : MutableLiveData<DataState<List<Submit>>> = MutableLiveData()
    val submitsState : LiveData<DataState<List<Submit>>>
        get() = _submitsState

    private val _downloadState : MutableLiveData<DataState<String>> = MutableLiveData()
    val downloadState : LiveData<DataState<String>>
        get() = _downloadState

    private val _commentsState : MutableLiveData<DataState<List<Comment>>> = MutableLiveData()
    val commentsState : LiveData<DataState<List<Comment>>>
        get() = _commentsState

    private val _notificationsState : MutableLiveData<DataState<List<Notification>>> = MutableLiveData()
    val notificationsState : LiveData<DataState<List<Notification>>>
        get() = _notificationsState

    private val _friendRequestsState : MutableLiveData<DataState<List<User>>> = MutableLiveData()
    val friendRequestsState : LiveData<DataState<List<User>>>
        get() = _friendRequestsState

    private var _isFriend: MutableLiveData<Boolean> = MutableLiveData()
    val isFriend : LiveData<Boolean>
        get() = _isFriend

    private val _userProfile : MutableLiveData<UserProfile> = MutableLiveData()
    val userProfile : LiveData<UserProfile>
        get() = _userProfile

    private val _activitiesState : MutableLiveData<DataState<List<ActivityItem>>> = MutableLiveData()
    val activitiesState : LiveData<DataState<List<ActivityItem>>>
        get() = _activitiesState

    private val _classesState : MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val classesState : LiveData<DataState<List<String>>>
        get() = _classesState

    private val _subscribeState : MutableLiveData<DataState<String>> = MutableLiveData()
    val subscribeState : LiveData<DataState<String>>
        get() = _subscribeState


    fun getUsers(query:String) = viewModelScope.launch {
        repository.getUsers(query)
                .collect {
                    _userState.value = it
                }

    }
    fun getContacts() = viewModelScope.launch {
        repository.getContacts()
                .collect {
                    _contactState.value = it
                }
    }
    fun getContact(receiverUid: String) = viewModelScope.launch {
        _contact.postValue(repository.getContact(receiverUid))
    }

    /*fun createChat(receiverUid:String) = viewModelScope.launch {
        _chatId.postValue(repository.createContact(receiverUid))
    }*/

    fun sendMessage(chatId: String,message: String) = viewModelScope.launch {
        repository.sendMessage(chatId,message)
    }

    @ExperimentalCoroutinesApi
    fun getMessages(chatId:String) = viewModelScope.launch {
        repository.getMessages(chatId)
            .collect {
                _messagesState.value = it
            }
    }
    fun getUser(uid:String) = viewModelScope.launch {
        repository.getUser(uid)
            .collect {
                _userProfileState.value = it
            }
    }

    /*fun updateLastMessage(contact: Contact,lastMessage:Message) = viewModelScope.launch {
            repository.updateLastMessage(contact,lastMessage)
    }*/

    fun getFeed() = viewModelScope.launch {
        repository.getFeed()
            .collect{
                _feedState.value = it
            }
    }
    @ExperimentalCoroutinesApi
    fun downloadFile(submit: Submit) = viewModelScope.launch {
        repository.downloadFile(submit)
                .collect {
                    _downloadState.value = it
                }
    }
    fun getSubmissions(feedItem: FeedItem) = viewModelScope.launch {
        repository.getSubmissions(feedItem)
                .collect{
                    _submitsState.value = it
                }
    }

    @ExperimentalCoroutinesApi
    fun getComments(feedItem: FeedItem) = viewModelScope.launch {
        repository.getComments(feedItem)
                .collect{
                    _commentsState.value = it
                }
    }
    fun addComment(feedItem: FeedItem,comment: String) = viewModelScope.launch {
        repository.addComment(feedItem,comment)
    }
    @ExperimentalCoroutinesApi
    fun getNotifications() = viewModelScope.launch {
        repository.getNotifications()
                .collect{
                    _notificationsState.value = it
                }
    }

    @ExperimentalCoroutinesApi
    fun getFriendRequests() = viewModelScope.launch {
        repository.getFriendRequests()
            .collect{
                _friendRequestsState.value = it
            }
    }

    fun handleFriendRequest(user: User,situation:Boolean) = viewModelScope.launch {
        if (situation){
            repository.addFriend(user)
        }else{
            repository.denyFriendRequest(user)
        }
    }

    fun sendFriendRequest(user: UserProfile) = viewModelScope.launch {
        repository.sendFriendRequest(user)
    }

    fun isFriend(user: UserProfile) = viewModelScope.launch {
        _isFriend.postValue(repository.isFriend(user))
    }
    fun getActivities(user: UserProfile) = viewModelScope.launch {
        repository.getActivities(user)
            .collect {
                _activitiesState.value = it
            }
    }
    fun getUserProfile() = viewModelScope.launch {
        _userProfile.postValue(repository.getUserProfile())
    }
    fun getUserProfile(uid: String) = viewModelScope.launch {
        _userProfile.postValue(repository.getUserProfile(uid))
    }
    fun dummy() = viewModelScope.launch {
        repository.dummyData()
    }
    fun getClassList() = viewModelScope.launch {
        repository.getClassList()
            .collect{
                _classesState.value = it
            }
    }
    fun subscribeToClass(className: String) = viewModelScope.launch {
        repository.subscribeToChannel(className)
            .collect{
                _subscribeState.value = it
            }
    }


}