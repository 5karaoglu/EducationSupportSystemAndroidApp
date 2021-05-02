package com.example.ess.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.model.UserShort
import com.example.ess.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonViewModel
    @Inject constructor(private val repository: CommonRepository): ViewModel() {

    private var _userState: MutableLiveData<DataState<List<UserShort>>> = MutableLiveData()
    val userState : LiveData<DataState<List<UserShort>>>
        get() = _userState


    fun getUsers(query:String) = viewModelScope.launch {
        repository.getUsers(query)
                .collect {
                    _userState.value = it
                }

    }


}