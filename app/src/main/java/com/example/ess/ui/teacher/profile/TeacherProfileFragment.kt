package com.example.ess.ui.teacher.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.databinding.FragmentTeacherProfileBinding
import com.example.ess.ui.teacher.TeacherViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherProfileFragment : Fragment() {

    private var _binding: FragmentTeacherProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeacherViewModel by viewModels()

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
            findNavController().navigate(R.id.action_teacherProfileFragment_to_teacherEditProfileFragment)
        }
        binding.ibAdd.setOnClickListener {
            // TODO: 4/29/2021 set add friend
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.currentUser.observe(viewLifecycleOwner){ user ->
            Picasso.get()
                .load(user.imageURL)
                .centerInside().fit()
                .into(binding.ivPp)

            binding.tvName.text = user.name
            binding.tvProfileHeader.text = user.email
        }
        viewModel.getUserInfo()
    }
}