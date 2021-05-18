package com.example.ess.ui.common.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.databinding.FragmentSearchBinding
import com.example.ess.model.User
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(),
SearchAdapter.OnItemClickListener{

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()
    private val TAG = "Search Fragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SearchAdapter(this)
        binding.recyclerSearch.adapter = adapter
        binding.recyclerSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearch.addItemDecoration(SearchItemDecoration())

        binding.ibSearch.setOnClickListener {
            viewModel.getUsers(binding.etSearch.text.toString())
        }

        viewModel.userState.observe(viewLifecycleOwner){
            when(it){
                is DataState.Loading -> binding.apply {
                    recyclerSearch.visibility = View.GONE
                    tvEmpty.visibility = View.GONE
                    pb.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Error ! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                    binding.apply {
                        recyclerSearch.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                        pb.visibility = View.GONE
                    }
                }
                is DataState.Success -> {
                    adapter.submitList(it.data)
                    binding.apply {
                        recyclerSearch.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE
                        pb.visibility = View.GONE
                    }
                }
                is DataState.Empty -> binding.apply {
                    recyclerSearch.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    pb.visibility = View.GONE
                }
            }
        }
    }

    override fun onItemClicked(user: User) {
        val action = SearchFragmentDirections.actionSearchFragmentToShowProfileFragment(user,null)
        findNavController().navigate(action)
    }
}