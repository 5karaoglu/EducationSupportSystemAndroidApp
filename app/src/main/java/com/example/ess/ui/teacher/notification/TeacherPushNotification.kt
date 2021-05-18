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
import com.example.ess.databinding.FragmentTeacherPushNotificationBinding
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherPushNotification : Fragment() {

    private var _binding: FragmentTeacherPushNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeacherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherPushNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.sendNotification()
       /* binding.apply {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonPushNotification.setOnClickListener {
                viewModel.pushNotification(spinnerTeacherChannels.selectedItem.toString(),etTitle.text.toString(),etDescription.text.toString())
            }
            teacherSpinnerLoad(spinnerTeacherChannels,pb,buttonPushNotification)
        }
        viewModel.getAllNotificationChannels()
        viewModel.getUserNotificationChannels()*/

    }

  /*  private fun teacherSpinnerLoad(spinner: Spinner,pb: ProgressBar,addChannel: Button){
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
    }*/

}