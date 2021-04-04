package com.example.ess.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ess.EssRepository
import com.example.ess.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel
    @Inject constructor(
        private val repository: EssRepository
    )
    : ViewModel() {

        fun insertNotificationToFirebase(channel: String, notification: Notification) = viewModelScope.launch{
            repository.insertNotificationToFirebase(channel, notification)
        }
}