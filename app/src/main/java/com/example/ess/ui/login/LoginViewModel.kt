package com.example.ess.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.util.DataState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(private val repository: LoginRepository): ViewModel(){

    private val TAG = "LoginViewModel"

    private var _dataState : MutableLiveData<DataState<*>?> = MutableLiveData()
    val dataState : LiveData<DataState<*>?>
        get() = _dataState




    fun signIn(email: String, password: String) = viewModelScope.launch{
        Log.d(TAG, "signIn: $email, $password")
        repository.signIn(email, password)
            .catch {
                dataState -> _dataState.value = DataState.Error(dataState)
            }
            .collect {
                    dataState -> _dataState.value = dataState
                Log.d("sup", "signIn: here")
            }

    }
}