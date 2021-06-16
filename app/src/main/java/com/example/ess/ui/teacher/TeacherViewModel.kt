package com.example.ess.ui.teacher

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.model.FeedItem
import com.example.ess.model.Notification
import com.example.ess.model.Submit
import com.example.ess.model.User
import com.example.ess.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel
@Inject constructor(private val repository: TeacherRepository) : ViewModel() {

    private val _allState: MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val allState: LiveData<DataState<List<String>>>
        get() = _allState

    private val _userState: MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val userState: LiveData<DataState<List<String>>>
        get() = _userState

    private val _notState: MutableLiveData<DataState<List<Notification>>> = MutableLiveData()
    val notState: LiveData<DataState<List<Notification>>>
        get() = _notState

    private val _updateState: MutableLiveData<DataState<String>> = MutableLiveData()
    val updateState: LiveData<DataState<String>>
        get() = _updateState

    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User>
        get() = _currentUser

    private val _uri: MutableLiveData<Uri> = MutableLiveData()
    val uri: LiveData<Uri>
        get() = _uri

    private val _classList: MutableLiveData<List<String>> = MutableLiveData()
    val classList: LiveData<List<String>>
        get() = _classList

    private val _issueList: MutableLiveData<List<String>> = MutableLiveData()
    val issueList: LiveData<List<String>>
        get() = _issueList

    private val _issue: MutableLiveData<FeedItem> = MutableLiveData()
    val issue: LiveData<FeedItem>
        get() = _issue

    private val _issueKey: MutableLiveData<String> = MutableLiveData()
    val issueKey: LiveData<String>
        get() = _issueKey

    private val _fileUploadState: MutableLiveData<DataState<String>> = MutableLiveData()
    val fileUploadState: LiveData<DataState<String>>
        get() = _fileUploadState

    private val _issueUpdateState: MutableLiveData<DataState<String>> = MutableLiveData()
    val issueUpdateState: LiveData<DataState<String>>
        get() = _issueUpdateState

    private val _feedState: MutableLiveData<DataState<List<FeedItem>>> = MutableLiveData()
    val feedState: LiveData<DataState<List<FeedItem>>>
        get() = _feedState


    private val _submitsState: MutableLiveData<DataState<List<Submit>>> = MutableLiveData()
    val submitsState: LiveData<DataState<List<Submit>>>
        get() = _submitsState

    fun pushNotification(channelName: String, notificationTitle: String, description: String) = viewModelScope.launch {
        repository.pushNotification(channelName, notificationTitle, description)
    }

    fun subscribeToChannel(channelName: String) = viewModelScope.launch {
        repository.subscribeToChannel(channelName)
    }

    fun getAllNotificationChannels() = viewModelScope.launch {
        repository.getAllNotificationChannels()
                .catch { notState ->
                    _allState.value = DataState.Error(notState)
                }.collect { notState ->
                    _allState.value = notState
                }
    }

    fun getUserNotificationChannels() = viewModelScope.launch {
        repository.getUserNotificationChannels()
                .catch {
                    _userState.value = DataState.Error(it)
                }.collect {
                    _userState.value = it
                }
    }

    fun getAllNotifications() = viewModelScope.launch {
        repository.getNotifications()
                .catch {
                    _notState.value = DataState.Error(it)
                }.collect {
                    _notState.value = it
                }
    }

    fun getUserInfo() = viewModelScope.launch {
        _currentUser.postValue(repository.getUserInfo())
    }

    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
                .collect {
                    _updateState.value = it
                }
    }

    fun uploadPhoto(uri: Uri?) = viewModelScope.launch {
        _uri.postValue(repository.uploadPhoto(uri))
    }

    fun getClassList() = viewModelScope.launch {
        _classList.postValue(repository.getClassList())
    }

    fun getIssueList(className: String) = viewModelScope.launch {
        _issueList.postValue(repository.getIssueList(className))
    }

    fun getIssue(className: String, issue: String) = viewModelScope.launch {
        _issue.postValue(repository.getIssue(className, issue))
    }

    @ExperimentalCoroutinesApi
    fun uploadFile(uri: Uri, className: String, issue: String, fileName: String) = viewModelScope.launch {
        repository.uploadFile(uri, className, issue, fileName)
                .collect {
                    _fileUploadState.value = it
                }
    }

    fun updateIssue(className: String, title: String, description: String, deadline: String, fileName: String?, downloadUrl: String?) = viewModelScope.launch {
        repository.updateIssue(className, title, description, deadline, fileName, downloadUrl)
                .collect {
                    _issueUpdateState.value = it
                }
    }

    fun createIssue(className: String, title: String, description: String, deadline: String, fileName: String?, downloadUrl: String?) = viewModelScope.launch {
        repository.createIssue(className, title, description, deadline, fileName, downloadUrl)
                .collect {
                    _issueUpdateState.value = it
                }
    }

    fun getIssueKey(selectedClass: String) = viewModelScope.launch {
        _issueKey.postValue(repository.getIssueKey(selectedClass))
    }

    fun getFeed() = viewModelScope.launch {
        repository.getFeed()
                .collect {
                    _feedState.value = it
                }
    }

    fun getSubmits(feedItem: FeedItem) = viewModelScope.launch {
        repository.getSubmits(feedItem)
                .collect {
                    _submitsState.value = it
                }
    }

    fun sendNotification() = viewModelScope.launch {
        repository.pushNotification()
    }
}