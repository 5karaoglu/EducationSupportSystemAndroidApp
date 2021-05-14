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

    private var _userState: MutableLiveData<DataState<List<UserShort>>> = MutableLiveData()
    val userState : LiveData<DataState<List<UserShort>>>
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


}