package com.example.ess.ui.teacher.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.databinding.FragmentTeacherProfileBinding
import com.example.ess.model.User
import com.example.ess.ui.teacher.TeacherViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherProfileFragment : Fragment() {

    private var _binding: FragmentTeacherProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeacherViewModel by viewModels()
    var cUser: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibEdit.setOnClickListener {
            val action = TeacherProfileFragmentDirections.actionTeacherProfileFragmentToEditProfileFragment(cUser!!)
            findNavController().navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.currentUser.observe(viewLifecycleOwner){ user ->
            cUser = user
            Picasso.get()
                .load(user.imageUrl)
                .centerInside().fit()
                .into(binding.ivPp)

            binding.tvName.text = user.name
            binding.tvProfileHeader.text = user.email
        }
        viewModel.getUserInfo()
    }
}