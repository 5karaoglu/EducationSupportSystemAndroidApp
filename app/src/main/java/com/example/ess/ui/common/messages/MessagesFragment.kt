package com.example.ess.ui.common.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.R
import com.example.ess.databinding.FragmentMessagesBinding
import com.example.ess.model.Contact
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment(), ContactsAdapter.OnItemClickListener {

    private var _binding : FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater,container,false)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        bottomBar.visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ContactsAdapter(this)
        binding.recyclerMessages.adapter = adapter
        binding.recyclerMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMessages.addItemDecoration(NotificationItemDecoration())
        binding.btnBack.setOnClickListener {
            val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
            bottomBar.visibility = View.VISIBLE
            findNavController().popBackStack()
        }

        viewModel.contactState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> binding.pb.visibility = View.VISIBLE
                is DataState.Error -> {
                    binding.pb.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                }
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
        viewModel.getContacts()

    }

    override fun onItemClicked(contact: Contact) {
        val action = MessagesFragmentDirections.actionMessagesFragmentToChatFragment(contact)
        findNavController().navigate(action)
    }


}