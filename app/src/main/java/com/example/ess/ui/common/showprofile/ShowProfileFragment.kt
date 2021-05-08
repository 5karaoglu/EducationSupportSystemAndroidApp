package com.example.ess.ui.common.showprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ess.R
import com.example.ess.databinding.FragmentSearchBinding
import com.example.ess.databinding.FragmentShowProfileBinding
import com.example.ess.model.User
import com.example.ess.model.UserShort
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowProfileFragment : Fragment() {

    private var _binding : FragmentShowProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()
    private val navArgs: ShowProfileFragmentArgs by navArgs()
    private var cUser: User? = null
    private var userShort: UserShort? = null
    private val TAG = "ShowProfile Fragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userShort = if (navArgs.userShort != null){
            navArgs.userShort
        }else{
            navArgs.feedItem!!.let {
                UserShort(
                        name = it.publishedBy,
                        imageURL = it.publisherImageUrl.toString(),
                        uid = it.publisherUid.toString()
                )
            }
        }
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ibMail.setOnClickListener {
            viewModel.getContact(cUser!!.uid!!)
            viewModel.contact.observe(viewLifecycleOwner){
                val action = ShowProfileFragmentDirections.actionShowProfileFragmentToChatFragment(it)
                findNavController().navigate(action)
            }
        }
        userShort?.let { viewModel.getUser(it.uid) }
        viewModel.userProfileState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {}
                is DataState.Error -> Toast.makeText(
                    requireContext(),
                    "Error! ${it.throwable.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                is DataState.Success -> {
                    cUser = it.data
                    Log.d(TAG, "onViewCreated: ${cUser!!.uid}")
                    initProfile(it.data)
                }
            }
        }

    }
    private fun initProfile(user: User){
        binding.tvName.text = user.name
        binding.tvProfileHeader.text = user.email
        Picasso.get()
            .load(user.imageURL)
            .fit().centerInside()
            .into(binding.ivPp)
    }
}