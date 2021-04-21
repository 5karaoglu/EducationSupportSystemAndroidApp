package com.example.ess.ui.student.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.ui.student.StudentViewModel
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentAddChannel : Fragment(R.layout.fragment_student_add_channel) {

    private val viewModel: StudentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addChannel = requireActivity().findViewById<Button>(R.id.btnStudentAddChannel)
        val pb = requireActivity().findViewById<ProgressBar>(R.id.pbStudentAddChannel)
        val spinner = requireActivity().findViewById<Spinner>(R.id.spinnerChannels)
        val btnBack = requireActivity().findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getChannels()
        spinnerLoad(spinner,pb,addChannel)
        buttonClick(spinner,pb,addChannel)

    }
    private fun buttonClick(spinner: Spinner,pb: ProgressBar,addChannel: Button){
        addChannel.setOnClickListener {
            viewModel.addChannel(spinner.selectedItem.toString())
        }
        viewModel.dataState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    pb.visibility = View.VISIBLE
                    addChannel.visibility = View.INVISIBLE
                }
                is DataState.Error -> {
                    if (pb.visibility == View.VISIBLE) {
                        pb.visibility = View.GONE
                        addChannel.visibility = View.VISIBLE
                    }
                    Toast.makeText(
                        requireContext(),
                        "Error ! ${it.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Success -> {
                    if (pb.visibility == View.VISIBLE) {
                        pb.visibility = View.GONE
                        addChannel.visibility = View.VISIBLE
                    }
                    Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                }}
        }
    }

    private fun spinnerLoad(spinner: Spinner,pb: ProgressBar,addChannel: Button){
        viewModel.loadState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    pb.visibility = View.VISIBLE
                    addChannel.visibility = View.INVISIBLE
                }
                is DataState.Error -> {
                    if (pb.visibility == View.VISIBLE) {
                        pb.visibility = View.GONE
                        addChannel.visibility = View.VISIBLE
                    }
                    Toast.makeText(
                        requireContext(),
                        "Error ! ${it.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Success -> {
                    if (pb.visibility == View.VISIBLE){
                        pb.visibility = View.GONE
                        addChannel.visibility = View.VISIBLE
                    }
                    val adapter = ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        it.data
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            }
        }
    }

}