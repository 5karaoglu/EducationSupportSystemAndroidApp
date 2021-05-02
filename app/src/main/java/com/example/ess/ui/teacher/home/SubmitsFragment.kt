package com.example.ess.ui.teacher.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.databinding.FragmentSubmitsBinding
import com.example.ess.ui.teacher.TeacherViewModel


class SubmitsFragment : Fragment() {

    private var _binding: FragmentSubmitsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeacherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubmitsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}