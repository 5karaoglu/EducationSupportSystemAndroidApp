package com.example.ess.ui.admin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ess.databinding.FragmentAdminNotificationsBinding
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminNotificationsFragment : Fragment() {

    private val TAG = "AdminNotificationsFragment"
    private val viewModel: AdminViewModel by viewModels()
    private var _binding: FragmentAdminNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            getNotState(spinnerChannels)
            buttonCreateNotificationChannel.setOnClickListener {
                val channelName = etNotificationChannel.text.toString()

                if (channelName.isNotEmpty()) {
                    AlertDialog.Builder(requireContext())
                            .setTitle("Are you sure you want to create new channel named '$channelName' ?")
                            .setPositiveButton("YES") { _, _ ->
                                viewModel.createNotificationChannel(
                                        channelName
                                )
                            }
                            .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
                            .setCancelable(false)
                            .create().show()

                } else {
                    Toast.makeText(requireContext(), "Not valid channel name !", Toast.LENGTH_SHORT)
                            .show()
                }
            }
            buttonPushNotification.setOnClickListener {
                val channelName = spinnerChannels.selectedItem.toString()
                val notification = etPushNotification.text.toString()

                viewModel.pushNotificationToChannel(channelName, notification)
            }
        }

        viewModel.channelState.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Success -> {
                    Toast.makeText(
                            requireContext(),
                            "Channel named '${it.data}' successfully created !",
                            Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Error -> {
                    Toast.makeText(
                            requireContext(),
                            "Error on creating new channel: ${it.throwable.message}",
                            Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: Loading...")
                }
            }
        })
    }

    private fun getNotState(spinner: Spinner) {
        viewModel.getNotificationChannels()
        var adapter: ArrayAdapter<String>


        viewModel.notState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, it.data)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
                is DataState.Error -> {
                    Log.d(TAG, "getNotState: ${it.throwable.message}")
                }
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: Loading...")
                }
            }
        }
    }
}