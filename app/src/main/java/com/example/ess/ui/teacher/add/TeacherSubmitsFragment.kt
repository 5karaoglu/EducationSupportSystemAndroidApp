package com.example.ess.ui.teacher.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ess.R
import com.example.ess.databinding.FragmentTeacherAddBinding
import com.example.ess.databinding.FragmentTeacherSubmitsBinding
import com.example.ess.ui.teacher.TeacherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherSubmitsFragment : Fragment() {

    private val viewModel : TeacherViewModel by viewModels()
    private var _binding : FragmentTeacherSubmitsBinding? = null
    private val binding get() = _binding!!
    private var selectedIssue: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherSubmitsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = requireArguments()
        selectedIssue = bundle.getString("selectedIssue")
    }
}