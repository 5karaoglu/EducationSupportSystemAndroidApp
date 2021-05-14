package com.example.ess.ui.common.home.comments

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
import com.example.ess.model.Comment
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class CommentsFragment : Fragment(),
    CommentsAdapter.OnItemClickListener {

    private val TAG = "CommentsFragment"
    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()
    private val args: CommentsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentsBinding.inflate(inflater,container,false)
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
        bottomBar.visibility = View.GONE
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CommentsAdapter(this)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.addItemDecoration(NotificationItemDecoration())
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonComment.setOnClickListener {
            if (binding.etAddComment.text.isNotEmpty()){
                viewModel.addComment(args.feedItem!!,binding.etAddComment.text.toString())
                binding.etAddComment.text.clear()
            }
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
                    adapter.submitList(it.data)
                }
            }
        }
    }



    override fun onTvClicked(comment: Comment) {
    }
}