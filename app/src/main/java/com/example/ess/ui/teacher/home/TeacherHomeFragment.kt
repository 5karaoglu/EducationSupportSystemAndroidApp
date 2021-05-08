package com.example.ess.ui.teacher.home

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
import com.example.ess.databinding.FragmentTeacherHomeBinding
import com.example.ess.model.FeedItem
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherHomeFragment : Fragment(),
    FeedAdapter.OnItemClickListener{

    private val TAG = "TeacherHomeFragment"
    private val viewModel : TeacherViewModel by viewModels()
    private var _binding : FragmentTeacherHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnMessages.setOnClickListener {
            findNavController().navigate(R.id.action_teacherHomeFragment_to_messagesFragment)
        }
        val adapter = FeedAdapter(this)
        binding.recyclerTeacherNotifications.adapter = adapter
        binding.recyclerTeacherNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTeacherNotifications.addItemDecoration(NotificationItemDecoration())

        viewModel.getFeed()
        viewModel.feedState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                }
                is DataState.Error -> Toast.makeText(requireContext(), "Error! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                is DataState.Success -> adapter.submitList(it.data)
            }
        }
    }


    override fun onViewClicked(feedItem: FeedItem) {

    }

    override fun onCommentsClicked(feedItem: FeedItem) {
        val action = TeacherHomeFragmentDirections.actionTeacherHomeFragmentToCommentsFragment(feedItem)
        findNavController().navigate(action)
    }

    override fun onSubscriptionsClicked(feedItem: FeedItem) {
        val action = TeacherHomeFragmentDirections.actionTeacherHomeFragmentToSubmitsFragment(feedItem)
        findNavController().navigate(action)
    }

    override fun onIvClicked(feedItem: FeedItem) {
        val action = TeacherHomeFragmentDirections.actionTeacherHomeFragmentToShowProfileFragment(null,feedItem)
        findNavController().navigate(action)
    }

}