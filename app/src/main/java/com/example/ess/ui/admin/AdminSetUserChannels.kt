package com.example.ess.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ess.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminSetUserChannels : Fragment(R.layout.fragment_admin_set_user_channels) {

    private val viewModel: AdminViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}