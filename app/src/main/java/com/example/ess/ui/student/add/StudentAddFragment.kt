package com.example.ess.ui.student.add

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.ui.student.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentAddFragment : Fragment(R.layout.fragment_student_add) {

    private val viewModel : StudentViewModel by viewModels()

}