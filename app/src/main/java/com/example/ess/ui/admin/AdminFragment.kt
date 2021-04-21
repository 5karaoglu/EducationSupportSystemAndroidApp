package com.example.ess.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AdminFragment : Fragment(R.layout.fragment_admin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPushNotifications = requireActivity().findViewById<Button>(R.id.btnPushNotification)
        val btnAddUser = requireActivity().findViewById<Button>(R.id.btnAddUser)
        val btnSetUserChannels = requireActivity().findViewById<Button>(R.id.btnSetUserChannels)

        btnPushNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_adminNotificationsFragment)
        }

        btnAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_addUserFragment)
        }
        btnSetUserChannels.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_adminSetUserChannels)
        }
    }

}