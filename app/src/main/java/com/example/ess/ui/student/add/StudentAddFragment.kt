package com.example.ess.ui.student.add

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.databinding.FragmentStudentAddBinding
import com.example.ess.model.Submit
import com.example.ess.ui.student.StudentViewModel
import com.example.ess.util.DataState
import com.example.ess.util.Functions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class StudentAddFragment : Fragment() {

    private val TAG = "StudentAddFragment"
    private val viewModel : StudentViewModel by viewModels()
    private var _binding : FragmentStudentAddBinding? = null
    private val binding get() = _binding!!
    private var selectedIssue: String? = null
    private var selectedClass: String? = null
    private var uri: Uri? = null
    private lateinit var files : ActivityResultLauncher<String>
    private var fileName : String? = null
    private var submit: Submit? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStudentAddBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        files = registerForActivityResult(ActivityResultContracts.GetContent()){
            Log.d("debug", "onCreate: $selectedClass,$selectedIssue")
            fileName = it.lastPathSegment
            uri = it
            binding.tvFileName.text = fileName
        }
    }

    @SuppressLint("SetTextI18n")
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            if (selectedClass.isNullOrEmpty() || selectedIssue.isNullOrEmpty() ||
                    uri == null || fileName.isNullOrEmpty()){
                Toast.makeText(
                    requireContext(),
                    "You have to fill all fields!",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                viewModel.submitToIssue(uri!!,selectedClass!!,selectedIssue!!,fileName!!)
            }
        }
        viewModel.submittingState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: Loading")
                    binding.pb.visibility = View.VISIBLE
                }
                is DataState.Error -> Toast.makeText(
                    requireContext(),
                    "Error! ${it.throwable.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                is DataState.Progress -> {
                    binding.pbUpload.progress = it.progress.toInt()
                }
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(requireContext(), "Upload successful !", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.homeFragment2)
                }
            }
        }
        binding.btnShowSubmits.setOnClickListener {
           /* findNavController().navigate(R.id.studenadd,
                bundleOf(Pair("selectedIssue",selectedIssue))
            )*/
        }
        binding.btnShowComments.setOnClickListener {
           /* findNavController().navigate(R.id.action_teacherAddFragment_to_teacherSubmitsFragment,
                bundleOf(Pair("selectedIssue",selectedIssue))
            )*/
        }
        binding.btnChooseFile.setOnClickListener {
            files.launch("application/pdf")
        }
        binding.btnEdit.setOnClickListener {
            if (selectedClass.isNullOrEmpty() || selectedIssue.isNullOrEmpty() ||
                    uri == null || fileName.isNullOrEmpty()){
                Toast.makeText(
                        requireContext(),
                        "You have to fill all fields!",
                        Toast.LENGTH_SHORT
                ).show()
            }else{
                viewModel.submitToIssue(uri!!,selectedClass!!,selectedIssue!!,fileName!!)
            }
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                    .setTitle("DELETE ${submit!!.fileName}")
                    .setNegativeButton("CANCEL",null)
                    .setPositiveButton("YES",DialogInterface.OnClickListener { _, _ ->
                        viewModel.deleteSubmission(submit!!)
                        findNavController().navigate(R.id.homeFragment2)
                    } ).show()
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
                    //checks if there is any submissions before
                    viewModel.checkSubmissions(selectedClass!!,selectedIssue!!)
                }
                binding.btnShowSubmits.isEnabled = true
                binding.btnShowComments.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        viewModel.submissionState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> {binding.pb.visibility = View.VISIBLE}
                is DataState.Error -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Error! ${it.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DataState.Success -> {
                    submit = it.data
                    binding.tvFileName.text = submit!!.fileName
                    binding.pb.visibility = View.GONE
                    binding.tvSubmitSituation.text =
                        "You submitted ${it.data!!.fileName} on ${Functions.tsToDate(it.data.timestamp)}"
                    binding.btnEdit.isEnabled = true
                    binding.btnDelete.isEnabled = true
                    binding.btnSubmit.isEnabled = false
                }
                is  DataState.Empty -> {
                    binding.pb.visibility = View.GONE
                    binding.tvSubmitSituation.text = "You haven't submitted yet."
                    binding.btnEdit.isEnabled = false
                    binding.btnDelete.isEnabled = false
                    binding.btnSubmit.isEnabled = true
                }
            }
        }
    }

}