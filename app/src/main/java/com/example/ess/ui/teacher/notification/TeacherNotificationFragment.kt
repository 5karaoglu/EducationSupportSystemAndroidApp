package com.example.ess.ui.teacher.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.ui.teacher.TeacherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherNotificationFragment : Fragment(R.layout.fragment_teacher_notification) {

    private val viewModel: TeacherViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAdd = requireActivity().findViewById<ImageButton>(R.id.btnPushNotification)
        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_teacherNotificationFragment_to_teacherPushNotification)
        }
    }
}