package com.example.ess.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.EssRepository
import com.example.ess.model.Notification
import com.example.ess.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel
    @Inject constructor(
        private val repository: StudentRepository
    )
    : ViewModel() {

        /*fun insertNotificationToFirebase(channel: String, notification: Notification) = viewModelScope.launch{
            repository.insertNotificationToFirebase(channel, notification)
        }*/

    private val _dataState : MutableLiveData<DataState<String>> = MutableLiveData()
    val dataState : LiveData<DataState<String>>
        get() = _dataState

    private val _loadState : MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val loadState : LiveData<DataState<List<String>>>
        get() = _loadState


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
}