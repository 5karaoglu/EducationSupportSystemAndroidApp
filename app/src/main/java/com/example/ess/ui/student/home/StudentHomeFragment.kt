package com.example.ess.ui.student.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.model.Notification
import com.example.ess.ui.student.StudentViewModel
import com.example.ess.util.Functions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentHomeFragment : Fragment(R.layout.fragment_student_home) {

    private val viewModel: StudentViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        /*viewModel.insertNotificationToFirebase("Meslek Semineri",
            Notification(1,"Dersler iptal", "Meslek Semineri",Functions.getCurrentDate()))*/
    }
}