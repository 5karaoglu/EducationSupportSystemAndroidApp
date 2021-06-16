package com.example.ess.ui.common.friendRequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.databinding.FragmentFriendRequestsBinding
import com.example.ess.model.User
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class FriendRequestsFragment : Fragment(),
        FriendRequestsAdapter.OnItemClickListener {

    private var _binding: FragmentFriendRequestsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FriendRequestsAdapter(this)
        binding.recycler.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(NotificationItemDecoration())
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getFriendRequests()
        viewModel.friendRequestsState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                }
                DataState.Empty -> TODO()
                is DataState.Error -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(
                            requireContext(),
                            "Error! ${it.throwable.message}",
                            Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
    }

    override fun onApproveClicked(user: User) {
        viewModel.handleFriendRequest(user, true)
    }

    override fun onDenyClicked(user: User) {
        viewModel.handleFriendRequest(user, false)
    }

    override fun onIvClicked(user: User) {
        val action = FriendRequestsFragmentDirections.actionFriendRequestsFragmentToShowProfileFragment(user, null)
        findNavController().navigate(action)
    }
}