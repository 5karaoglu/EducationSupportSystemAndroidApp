package com.example.ess.ui.common.home.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.databinding.FragmentSubscribeClassBinding
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscribeClassFragment : Fragment() {

    private var _binding: FragmentSubscribeClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscribeClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getClassList()
        viewModel.classesState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(
                            requireContext(),
                            "Error! ${it.throwable.message}",
                            Toast.LENGTH_SHORT
                    )
                            .show()
                }
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    val adapter = ArrayAdapter<String>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            it.data
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerTeacherChannels.adapter = adapter
                }
            }
        }
        binding.buttonSub.setOnClickListener {
            viewModel.subscribeToClass(binding.spinnerTeacherChannels.selectedItem.toString())
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.subscribeState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(
                            requireContext(),
                            "Error! ${it.throwable.message}",
                            Toast.LENGTH_SHORT
                    )
                            .show()
                }
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(
                            requireContext(),
                            "${it.data} successfully added!", Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        }
    }
}