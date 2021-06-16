package com.example.ess.ui.teacher.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ess.R
import com.example.ess.databinding.FragmentTeacherEditTitleBinding
import com.example.ess.ui.teacher.TeacherViewModel
import com.example.ess.util.DataState
import com.example.ess.util.Functions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@AndroidEntryPoint
class TeacherCreateIssueFragment : Fragment(),
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private val TAG = "TeacherCreateIssueFragment"
    private val viewModel: TeacherViewModel by viewModels()
    private var _binding: FragmentTeacherEditTitleBinding? = null
    private val binding get() = _binding!!
    private var selectedClass: String? = null
    val c = Calendar.getInstance()
    var year = c.get(Calendar.YEAR)
    var month = c.get(Calendar.MONTH)
    var day = c.get(Calendar.DAY_OF_MONTH)
    var hour = c.get(Calendar.HOUR_OF_DAY)
    var minute = c.get(Calendar.MINUTE)
    private var deadline: String? = null
    private lateinit var files: ActivityResultLauncher<String>
    private var fileName: String? = null
    private var downloadUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTeacherEditTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        files = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (!binding.etTitle.text.isNullOrEmpty()) {
                viewModel.uploadFile(it, selectedClass!!, binding.etTitle.text.toString(), it.lastPathSegment!!)
                fileName = it.lastPathSegment
                binding.tvFileName.text = fileName
            } else {
                Toast.makeText(requireContext(), "Error! Title cant be empty.", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedClass = requireArguments().getString("selectedClass")
        binding.btnChooseFile.setOnClickListener {
            chooseFile()
        }
        binding.tvDeadlineChange.setOnClickListener {
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.ibUpdate.setOnClickListener {
            if (!binding.etTitle.text.isNullOrEmpty() && !binding.etDes.text.isNullOrEmpty() &&
                    !deadline.isNullOrEmpty() && !selectedClass.isNullOrEmpty()) {
                viewModel.createIssue(selectedClass!!, binding.etTitle.text.toString(),
                        binding.etDes.text.toString(), deadline!!, fileName, downloadUrl)
            } else {
                Toast.makeText(requireContext(), "You have to fill all fields!", Toast.LENGTH_SHORT)
                        .show()
            }

        }
        viewModel.issueUpdateState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                }
                is DataState.Error -> Toast.makeText(requireContext(), "Error! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                is DataState.Success -> {
                    Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    binding.pb.visibility = View.GONE
                    findNavController().navigate(R.id.action_teacherCreateIssueFragment_to_teacherAddFragment)
                }
            }
        }
        viewModel.fileUploadState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                }
                is DataState.Error -> Toast.makeText(
                        requireContext(),
                        "Error! ${it.throwable.message}",
                        Toast.LENGTH_SHORT
                )
                        .show()
                is DataState.Progress -> binding.pb.progress = it.progress.toInt()
                is DataState.Success -> {
                    Toast.makeText(requireContext(), "File uploaded successfully !", Toast.LENGTH_SHORT)
                            .show()
                    downloadUrl = it.data
                }
            }
        }
    }

    private fun chooseFile() {
        files.launch("application/pdf")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.day = dayOfMonth
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        this.hour = hourOfDay
        this.minute = minute
        val cal = GregorianCalendar(this.year, this.month, this.day, this.hour, this.minute)
        deadline = cal.timeInMillis.toString()
        binding.tvDate.text = Functions.tsToDate(deadline!!)
    }
}