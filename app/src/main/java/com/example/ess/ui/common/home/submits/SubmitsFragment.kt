package com.example.ess.ui.common.home.submits

import android.app.ProgressDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.EssApplication
import com.example.ess.R
import com.example.ess.databinding.FragmentSubmitsBinding
import com.example.ess.model.Submit
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class SubmitsFragment : Fragment(), SubmitsAdapter.OnItemClickListener {

    private var _binding: FragmentSubmitsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()
    private val args: SubmitsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubmitsBinding.inflate(inflater,container,false)
        var bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        if (bottomBar == null){
            bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavStudent)
        }
        bottomBar.visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        var bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        if (bottomBar == null){
            bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavStudent)
        }
        bottomBar.visibility = View.VISIBLE
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SubmitsAdapter(this)
        binding.recycler.apply {
            addItemDecoration(NotificationItemDecoration())
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        args.feedItem?.let { viewModel.getSubmissions(it) }
        viewModel.submitsState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {}
                is DataState.Error -> Toast.makeText(requireContext(), "Error! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                is DataState.Success -> adapter.submitList(it.data).also {
                    Log.d("TAG", "onViewCreated: success")
                }
            }
        }
        viewModel.downloadState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                }
                is DataState.Error -> Toast.makeText(requireContext(), "Error! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                is DataState.Progress -> binding.pb.progress = it.progress.toInt()
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewClicked(submit: Submit) {
        if ((requireActivity().application as EssApplication).userType == "Teacher" ){
            viewModel.downloadFile(submit)
        }
    }
}