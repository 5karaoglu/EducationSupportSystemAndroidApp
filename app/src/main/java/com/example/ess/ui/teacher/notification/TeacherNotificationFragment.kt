package com.example.ess.ui.teacher.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.R
import com.example.ess.databinding.FragmentTeacherNotificationBinding
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherNotificationFragment : Fragment(R.layout.fragment_teacher_notification) {

    private val viewModel: TeacherViewModel by viewModels()
    private val TAG = "Teacher Notification Fragment"
    private var _binding : FragmentTeacherNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPushNotification.setOnClickListener {
            findNavController().navigate(R.id.action_teacherNotificationFragment_to_teacherPushNotification)
        }

        val adapter = TeacherNotificationAdapter()
        binding.recyclerTeacherNotifications.adapter = adapter
        binding.recyclerTeacherNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTeacherNotifications.addItemDecoration(NotificationItemDecoration())


        viewModel.notState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                    binding.pb.visibility = View.VISIBLE

                }
                is DataState.Error -> {
                    if (binding.pb.visibility == View.VISIBLE) {
                        binding.pb.visibility = View.GONE
                        binding.recyclerTeacherNotifications.visibility = View.VISIBLE
                    }
                    Toast.makeText(
                        requireContext(),
                        "Error ! ${it.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    binding.recyclerTeacherNotifications.visibility = View.VISIBLE
                    for (i in it.data) {
                        Log.d("debug", "onViewCreated: ${i.title}")
                    }
                    adapter.notifyDataSetChanged()
                    adapter.submitList(it.data)
                }
            }
        }
        viewModel.getAllNotifications()
    }

}