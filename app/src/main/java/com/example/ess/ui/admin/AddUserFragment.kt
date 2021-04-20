package com.example.ess.ui.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.util.DataState
import com.example.ess.util.Functions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddUserFragment : Fragment(R.layout.fragment_add_user) {

    private val viewModel: AdminViewModel by viewModels()
    private val TAG = "AddUserFragment"
    private lateinit var rbStudent: RadioButton
    private lateinit var rbTeacher: RadioButton
    private lateinit var rbAdmin: RadioButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonLogin = requireActivity().findViewById<Button>(R.id.buttonLogin)
        val etUsername = requireActivity().findViewById<EditText>(R.id.etUsername)
        val etPassword = requireActivity().findViewById<EditText>(R.id.etPassword)
        val radioGroup = requireActivity().findViewById<RadioGroup>(R.id.radioGroup)
        rbStudent = requireActivity().findViewById<RadioButton>(R.id.rbStudent)
        rbTeacher = requireActivity().findViewById<RadioButton>(R.id.rbTeacher)
        rbAdmin = requireActivity().findViewById<RadioButton>(R.id.rbAdmin)
        buttonLogin.setOnClickListener {

            if (!etUsername.text.isNullOrEmpty() && !etPassword.text.isNullOrEmpty()){

                val email = etUsername.text.toString()
                val password = etPassword.text.toString()
                val userType = getUserType(radioGroup.checkedRadioButtonId)

                viewModel.signUp(email,password,userType)

            }

        }

        viewModel.dataState.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "User created successfully !",
                        Toast.LENGTH_SHORT
                    ).show()
                    etUsername.text.clear()
                    etPassword.text.clear()
                    findNavController().navigate(R.id.action_addUserFragment_to_adminFragment)
                }
                is DataState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "User couldn't created ! ${it.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                }
            }
        })
    }
    private fun getUserType(type: Int):String {
        return when(type){
            rbStudent.id -> "Student"
            rbTeacher.id -> "Teacher"
            rbAdmin.id -> "Admin"
            else -> "Student"
        }
    }
}