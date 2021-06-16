package com.example.ess.ui.student

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class StudentViewModel
@Inject constructor(
        private val repository: StudentRepository
) : ViewModel() {


    private val _dataState: MutableLiveData<DataState<String>> = MutableLiveData()
    val dataState: LiveData<DataState<String>>
        get() = _dataState

    private val _loadState: MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val loadState: LiveData<DataState<List<String>>>
        get() = _loadState

    private val _classList: MutableLiveData<List<String>> = MutableLiveData()
    val classList: LiveData<List<String>>
        get() = _classList

    private val _issueList: MutableLiveData<List<String>> = MutableLiveData()
    val issueList: LiveData<List<String>>
        get() = _issueList

    private val _submissionState: MutableLiveData<DataState<Submit?>> = MutableLiveData()
    val submissionState: LiveData<DataState<Submit?>>
        get() = _submissionState

    private val _submittingState: MutableLiveData<DataState<String>> = MutableLiveData()
    val submittingState: LiveData<DataState<String>>
        get() = _submittingState

    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User>
        get() = _currentUser


    fun getUserInfo() = viewModelScope.launch {
        _currentUser.postValue(repository.getUserInfo())
    }


    fun getClassList() = viewModelScope.launch {
        _classList.postValue(repository.getClassList())
    }

    fun getIssueList(className: String) = viewModelScope.launch {
        _issueList.postValue(repository.getIssueList(className))
    }

    fun checkSubmissions(className: String, issueName: String) = viewModelScope.launch {
        repository.checkSubmissions(className, issueName)
                .collect {
                    _submissionState.value = it
                }
    }

    fun addChannel(channelName: String) = viewModelScope.launch {
        repository.addChannel(channelName).catch {
            _dataState.value = DataState.Error(it)
        }.collect {
            _dataState.value = it
        }
    }

    fun getChannels() = viewModelScope.launch {
        repository.getNotificationChannels().catch {
            _loadState.value = DataState.Error(it)
        }.collect {
            _loadState.value = it
        }
    }

    @ExperimentalCoroutinesApi
    fun submitToIssue(
            uri: Uri, className: String, issueName: String, fileName: String,
    ) = viewModelScope.launch {
        repository.submitToIssue(uri, className, issueName, fileName)
                .collect {
                    _submittingState.value = it
                }
    }

    fun deleteSubmission(submit: Submit) = viewModelScope.launch {
        repository.deleteSubmission(submit)
    }

}