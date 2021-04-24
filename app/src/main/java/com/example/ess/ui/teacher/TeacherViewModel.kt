package com.example.ess.ui.teacher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.model.Notification
import com.example.ess.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel
@Inject constructor(private val repository: TeacherRepository): ViewModel() {

    private val _allState : MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val allState : LiveData<DataState<List<String>>>
        get() = _allState

    private val _userState : MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val userState : LiveData<DataState<List<String>>>
        get() = _userState

    private val _notState : MutableLiveData<DataState<List<Notification>>> = MutableLiveData()
    val notState : LiveData<DataState<List<Notification>>>
        get() = _notState

    fun pushNotification(channelName: String, notificationTitle: String) = viewModelScope.launch {
        repository.pushNotification(channelName,notificationTitle)
    }

    fun subscribeToChannel(channelName: String) = viewModelScope.launch {
        repository.subscribeToChannel(channelName)
    }
    fun getAllNotificationChannels() = viewModelScope.launch {
        repository.getAllNotificationChannels()
            .catch {
                    notState -> _allState.value = DataState.Error(notState)
            }.collect{
                    notState -> _allState.value = notState
            }
    }
    fun getUserNotificationChannels() = viewModelScope.launch {
        repository.getUserNotificationChannels()
            .catch {
                _userState.value = DataState.Error(it)
            }.collect{
                _userState.value = it
            }
    }
    fun getAllNotifications() = viewModelScope.launch {
        repository.getNotifications()
            .catch {
                _notState.value = DataState.Error(it)
            }.collect{
                _notState.value = it
            }
    }
}