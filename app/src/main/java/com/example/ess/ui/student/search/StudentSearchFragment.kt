package com.example.ess.ui.student.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.ui.student.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentSearchFragment : Fragment(R.layout.fragment_student_search) {

    private val viewModel: StudentViewModel by viewModels()
}