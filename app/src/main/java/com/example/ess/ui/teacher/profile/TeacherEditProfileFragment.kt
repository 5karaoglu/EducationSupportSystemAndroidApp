package com.example.ess.ui.teacher.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.databinding.FragmentTeacherEditProfileBinding
import com.example.ess.model.User
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class TeacherEditProfileFragment : Fragment() {

    private val TAG = "TeacherEditProfileFragment"
    private var _binding : FragmentTeacherEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeacherViewModel by viewModels()
    private var uri: Uri? = null
    private var user: User? = null
    private lateinit var gallery : ActivityResultLauncher<String>

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gallery = registerForActivityResult(ActivityResultContracts.GetContent()){
            viewModel.uploadPhoto(it)
            Picasso.get().load(it).centerInside().fit().into(binding.ivPp)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherEditProfileBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvChangePhoto.setOnClickListener {
            gallery.launch("image/*")
        }
        viewModel.updateState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                }
                is DataState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Error! {${it.throwable.message}}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Success -> {
                    Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_teacherEditProfileFragment_to_teacherProfileFragment)
                }
            }
        }
        binding.ibUpdate.setOnClickListener {
            uri = viewModel.uri.value
            if (uri != null){
                val user = User(
                    name = binding.etName.text.toString(),
                    email = binding.etUsername.text.toString(),
                    imageURL = uri.toString()
                )
                viewModel.updateUser(user)
            }else{
                Toast.makeText(
                    requireContext(),
                    "Process not successful. Try again !",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        viewModel.currentUser.observe(viewLifecycleOwner){
            Log.d(TAG, "onViewCreated: ${it.imageURL}")
            user = it
            Picasso.get()
                    .load(it.imageURL)
                    .fit().centerInside()
                    .into(binding.ivPp)
            binding.etName.setText(it.name)
            binding.etUsername.setText(it.email)
        }
        viewModel.getUserInfo()

    }
}