package com.example.ess.ui.teacher.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.databinding.FragmentTeacherHomeBinding
import com.example.ess.ui.teacher.TeacherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherHomeFragment : Fragment() {

    private val viewModel : TeacherViewModel by viewModels()
    private var _binding : FragmentTeacherHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}