package com.example.ess.ui.teacher.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.databinding.FragmentTeacherAddBinding
import com.example.ess.databinding.FragmentTeacherHomeBinding
import com.example.ess.model.IssueShort
import com.example.ess.ui.teacher.TeacherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherAddFragment : Fragment() {

    private val viewModel : TeacherViewModel by viewModels()
    private var _binding : FragmentTeacherAddBinding? = null
    private val binding get() = _binding!!
    private var selectedIssue: String? = null
    private var selectedClass: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherAddBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateIssue.setOnClickListener {
                findNavController().navigate(R.id.action_teacherAddFragment_to_teacherCreateIssueFragment,
                        bundleOf(Pair("selectedClass",selectedClass)))
        }
        binding.btnShowSubmits.setOnClickListener {
            findNavController().navigate(R.id.action_teacherAddFragment_to_teacherSubmitsFragment,
                bundleOf(Pair("selectedIssue",selectedIssue)))
        }
        binding.btnShowComments.setOnClickListener {
            findNavController().navigate(R.id.action_teacherAddFragment_to_teacherSubmitsFragment,
                bundleOf(Pair("selectedIssue",selectedIssue)))
        }
        binding.btnEditIssue.setOnClickListener {
            findNavController().navigate(R.id.action_teacherAddFragment_to_teacherEditTitleFragment,
                bundleOf(Pair("selectedIssue",selectedIssue),
                Pair("selectedClass",selectedClass)
                ))
        }
        var adapter : ArrayAdapter<String>
        var adapter2: ArrayAdapter<String>
        viewModel.classList.observe(viewLifecycleOwner){
            adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerClasses.adapter = adapter
            binding.pb.visibility = View.GONE
        }
        viewModel.getClassList()
        binding.spinnerClasses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedClass = binding.spinnerClasses.selectedItem.toString()
                viewModel.getIssueList(selectedClass!!)
                binding.pb.visibility = View.VISIBLE
                binding.btnCreateIssue.isEnabled = true

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        viewModel.issueList.observe(viewLifecycleOwner){
            adapter2 = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,it)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerIssues.adapter = adapter2
            binding.pb.visibility = View.GONE
        }
        binding.spinnerIssues.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (binding.spinnerIssues.selectedItem != null){
                   selectedIssue = binding.spinnerIssues.selectedItem.toString()
                }
                binding.btnShowSubmits.isEnabled = true
                binding.btnShowComments.isEnabled = true
                binding.btnEditIssue.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

}