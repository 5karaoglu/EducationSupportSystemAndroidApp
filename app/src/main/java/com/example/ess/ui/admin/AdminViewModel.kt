package com.example.ess.ui.admin

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel
    @Inject constructor(
        private val repository: AdminRepository
    ): ViewModel() {

    private val _dataState : MutableLiveData<DataState<*>> = MutableLiveData()
    val dataState : LiveData<DataState<*>>
        get() = _dataState

    private val _channelState : MutableLiveData<DataState<*>> = MutableLiveData()
    val channelState : LiveData<DataState<*>>
        get() = _channelState

    private val _notState : MutableLiveData<DataState<List<String>>> = MutableLiveData()
    val notState : LiveData<DataState<List<String>>>
        get() = _notState


    fun signUp(email: String, password: String, userType: String) = viewModelScope.launch{
        repository.signUp(email, password, userType)
                .catch {
                    dataState -> _dataState.value = DataState.Error(dataState)
                }.collect {
                    dataState -> _dataState.value = dataState
                }
    }

    fun createNotificationChannel(channelName: String) = viewModelScope.launch {
        repository.createNotificationChannel(channelName)
                .catch {
                    dataState -> _channelState.value = DataState.Error(dataState)
                }.collect {
                    dataState -> _channelState.value = dataState
                }
    }

    fun getNotificationChannels() = viewModelScope.launch {
        repository.getNotificationChannels()
            .catch {
                notState -> _notState.value = DataState.Error(notState)
            }.collect{
                notState -> _notState.value = notState
            }
    }

    fun pushNotificationToChannel(channelName: String,channelTitle:String) = viewModelScope.launch {
        repository.pushNotificationToChannel(channelName,channelTitle)
    }
}