package com.example.ess.ui.teacher.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherPushNotification : Fragment(R.layout.fragment_teacher_push_notification) {

    private val viewModel: TeacherViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pb = requireActivity().findViewById<ProgressBar>(R.id.pb)
        val spinnerAllChannels = requireActivity().findViewById<Spinner>(R.id.spinnerAllChannels)
        val btnSubscribe = requireActivity().findViewById<Button>(R.id.btnSubscribeToChannel)
        val spinnerTeacherChannels = requireActivity().findViewById<Spinner>(R.id.spinnerTeacherChannels)
        val etNotification = requireActivity().findViewById<EditText>(R.id.etPushNotification)
        val btnPush = requireActivity().findViewById<Button>(R.id.buttonPushNotification)
        val btnBack = requireActivity().findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        allSpinnerLoad(spinnerAllChannels,pb,btnPush)
        teacherSpinnerLoad(spinnerTeacherChannels,pb,btnPush,etNotification)
        btnPush.setOnClickListener {
            viewModel.pushNotification(spinnerTeacherChannels.selectedItem.toString(),etNotification.text.toString())
        }
        btnSubscribe.setOnClickListener {
            viewModel.subscribeToChannel(spinnerAllChannels.selectedItem.toString())
        }
        viewModel.getAllNotificationChannels()
        viewModel.getUserNotificationChannels()

    }

    private fun allSpinnerLoad(spinner: Spinner,pb: ProgressBar,addChannel: Button){
        viewModel.allState.observe(viewLifecycleOwner){
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

    private fun teacherSpinnerLoad(spinner: Spinner,pb: ProgressBar,addChannel: Button,et:EditText){
        viewModel.userState.observe(viewLifecycleOwner){
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