package com.example.ess.ui.admin

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


        fun signUp(email: String, password: String, userType: String) = viewModelScope.launch{
            repository.signUp(email, password, userType)
                .catch {
                    dataState -> _dataState.value = DataState.Error(dataState)
                }.collect {
                        dataState -> _dataState.value = dataState
                }
        }
}