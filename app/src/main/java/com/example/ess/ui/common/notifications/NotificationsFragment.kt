package com.example.ess.ui.common.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.R
import com.example.ess.databinding.FragmentNotificationsBinding
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val viewModel: CommonViewModel by viewModels()
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddChannelFragment.setOnClickListener {
            findNavController().navigate(R.id.action_notificationsFragment2_to_teacherPushNotification)
        }
        viewModel.getNotifications()
        val adapter = NotificationsAdapter()
        binding.recyclerStudentNotifications.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(NotificationItemDecoration())
        }
        viewModel.notificationsState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Error! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                    binding.pb.visibility = View.GONE
                }
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }

    }
}