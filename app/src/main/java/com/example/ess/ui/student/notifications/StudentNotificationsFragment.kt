package com.example.ess.ui.student.notifications

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.ui.student.StudentViewModel
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentNotificationsFragment : Fragment(R.layout.fragment_student_notifications) {

    private val viewModel: StudentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAddChannelFragment = requireActivity().findViewById<ImageButton>(R.id.btnAddChannelFragment)
        btnAddChannelFragment.setOnClickListener {
            findNavController().navigate(R.id.action_studentNotificationsFragment_to_studentAddChannel)
        }
    }
}