package com.example.ess.ui.common.showprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.databinding.FragmentShowProfileBinding
import com.example.ess.model.User
import com.example.ess.model.UserProfile
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.ui.common.profile.ActivitiesAdapter
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowProfileFragment : Fragment() {

    private var _binding: FragmentShowProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()
    private val navArgs: ShowProfileFragmentArgs by navArgs()
    private var cUser: User? = null
    private var user: UserProfile? = null
    private var profileUid: String? = null
    private val TAG = "ShowProfile Fragment"
    private lateinit var adapter: ActivitiesAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ibBack.setOnClickListener {
                findNavController().popBackStack()
            }
            ibAdd.setOnClickListener {
                viewModel.sendFriendRequest(user!!)
            }
            ibContact.setOnClickListener {
                viewModel.getContact(profileUid!!)
            }
        }
        if (navArgs.user != null) {
            profileUid = navArgs.user!!.uid
            viewModel.getUserProfile(profileUid!!)
        } else {
            profileUid = navArgs.feedItem!!.publisherUid
            viewModel.getUserProfile(profileUid!!)
        }
        viewModel.userProfile.observe(viewLifecycleOwner) {
            user = it
            initProfile(user!!)

            viewModel.isFriend(user!!)
        }
        viewModel.isFriend.observe(viewLifecycleOwner) {
            if (it) {
                binding.apply {
                    ibAdd.isEnabled = false
                    recycler.visibility = View.VISIBLE
                    tvPrivate.visibility = View.GONE
                }
                viewModel.getActivities(user!!)
            } else {
                Log.d(TAG, "onViewCreated: Not friend")
            }
        }

        viewModel.contact.observe(viewLifecycleOwner) {
            val action = ShowProfileFragmentDirections.actionShowProfileFragmentToChatFragment(it)
            findNavController().navigate(action)
        }
        viewModel.activitiesState.observe(viewLifecycleOwner) { it ->
            when (it) {
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                }
                DataState.Empty -> {
                    Log.d(TAG, "onViewCreated: empty")
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Error getting activities ! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                }
                is DataState.Success -> {
                    Log.d(TAG, "onViewCreated: success")
                    adapter = ActivitiesAdapter(user!!)
                    binding.apply {
                        recycler.adapter = adapter
                        recycler.layoutManager = LinearLayoutManager(requireContext())
                        recycler.addItemDecoration(NotificationItemDecoration())
                    }
                    adapter.submitList(it.data)

                }
            }
        }


    }

    private fun initProfile(user: UserProfile) {
        binding.tvName.text = user.name
        binding.tvProfileHeader.text = user.email
        binding.tvFriendsCount.text = user.friendsCount
        binding.tvClassesCount.text = user.classesCount
        Picasso.get()
                .load(user.imageUrl)
                .fit().centerInside()
                .into(binding.ivPp)
    }
}