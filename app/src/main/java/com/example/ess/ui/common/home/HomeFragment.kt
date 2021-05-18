package com.example.ess.ui.common.home

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
import com.example.ess.databinding.FragmentHomeBinding
import com.example.ess.model.FeedItem
import com.example.ess.model.User
import com.example.ess.model.UserShort
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(),
FeedAdapter.OnItemClickListener{


    private val TAG = "HomeFragment"
    private val viewModel : CommonViewModel by viewModels()
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubscribe.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_subscribeClassFragment)
        }
        viewModel.dummy()
        binding.btnMessages.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_messagesFragment)
        }
        val adapter = FeedAdapter(this)
        binding.recyclerTeacherNotifications.adapter = adapter
        binding.recyclerTeacherNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTeacherNotifications.addItemDecoration(FeedItemDecoration())

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
        val action = HomeFragmentDirections.actionHomeFragmentToCommentsFragment(feedItem)
        findNavController().navigate(action)
    }

    override fun onSubmitsClicked(feedItem: FeedItem) {
        val action = HomeFragmentDirections.actionHomeFragmentToSubmitsFragment(feedItem)
        findNavController().navigate(action)
    }

    override fun onIvClicked(feedItem: FeedItem) {
        val user = User(feedItem.publisherUid,feedItem.publishedBy,"",feedItem.publisherImageUrl)
        val action = HomeFragmentDirections.actionHomeFragmentToShowProfileFragment(user,null)
        findNavController().navigate(action)
    }
}