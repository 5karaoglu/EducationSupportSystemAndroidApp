package com.example.ess.ui.student.notifications

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.ui.student.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentNotificationsFragment : Fragment(R.layout.fragment_student_notifications) {

    private val viewModel: StudentViewModel by viewModels()
}