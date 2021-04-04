package com.example.ess.ui.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.util.DataState
import com.example.ess.util.Functions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddUserFragment : Fragment(R.layout.fragment_add_user) {

    private val viewModel: AdminViewModel by viewModels()
    private val TAG = "AddUserFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonLogin = requireActivity().findViewById<Button>(R.id.buttonLogin)
        val etUsername = requireActivity().findViewById<EditText>(R.id.etUsername)
        val etPassword = requireActivity().findViewById<EditText>(R.id.etPassword)
        val radioGroup = requireActivity().findViewById<RadioGroup>(R.id.radioGroup)
        buttonLogin.setOnClickListener {

            if (!etUsername.text.isNullOrEmpty() && !etPassword.text.isNullOrEmpty()){

                val email = etUsername.text.toString()
                val password = etPassword.text.toString()
                val userType = Functions.getUserType(radioGroup.checkedRadioButtonId)

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
}