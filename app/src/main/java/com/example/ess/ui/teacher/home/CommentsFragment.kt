package com.example.ess.ui.teacher.home

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
import com.example.ess.R
import com.example.ess.databinding.FragmentCommentsBinding
import com.example.ess.databinding.FragmentSubmitsBinding
import com.example.ess.model.Comment
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : Fragment(),
CommentsAdapter.OnItemClickListener{

    private val TAG = "CommentsFragment"
    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeacherViewModel by viewModels()
    private val args: CommentsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentsBinding.inflate(inflater,container,false)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        bottomBar.visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        bottomBar.visibility = View.GONE
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CommentsAdapter(this)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.addItemDecoration(NotificationItemDecoration())
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getComments(args.feedItem!!)
        viewModel.commentsState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Error! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                }
                is DataState.Success -> {
                    Log.d(TAG, "onViewCreated: wow success!! ${it.data[0].comment}")
                    adapter.submitList(it.data)
                }
            }
        }
    }



    override fun onTvClicked(comment: Comment) {
        TODO("Not yet implemented")
    }
}