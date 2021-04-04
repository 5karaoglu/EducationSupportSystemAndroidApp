package com.example.ess.ui.student.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.ui.student.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentProfileFragment : Fragment(R.layout.fragment_student_profile) {

    private val viewModel: StudentViewModel by viewModels()
}