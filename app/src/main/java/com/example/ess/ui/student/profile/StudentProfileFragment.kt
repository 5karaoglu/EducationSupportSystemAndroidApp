package com.example.ess.ui.student.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.databinding.FragmentStudentProfileBinding
import com.example.ess.model.UserProfile
import com.example.ess.ui.student.StudentViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentProfileFragment : Fragment() {

        private var _binding: FragmentStudentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by viewModels()
    private  var cUser: UserProfile? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibEdit.setOnClickListener {
            val action = StudentProfileFragmentDirections.actionStudentProfileFragmentToEditProfileFragment(cUser!!)
            findNavController().navigate(action)
        }

        viewModel.userProfile.observe(viewLifecycleOwner){ user ->
            cUser = user
            Picasso.get()
                    .load(user.imageUrl)
                    .centerInside().fit()
                    .into(binding.ivPp)

            binding.tvName.text = user.name
            binding.tvProfileHeader.text = user.email
            binding.tvClassesCount.text = user.classesCount
            binding.tvFriendsCount.text = user.friendsCount
            viewModel.getActivities(user)
        }
        viewModel.activitiesState.observe(viewLifecycleOwner){ it ->
            when(it){
                is DataState.Loading -> {
                    binding.recycler.visibility = View.GONE
                    binding.pb.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                DataState.Empty -> {
                    binding.recycler.visibility = View.GONE
                    binding.pb.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    binding.recycler.visibility = View.GONE
                    binding.pb.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Error getting activities ! ${it.throwable.message}", Toast.LENGTH_SHORT).show()
                }
                is DataState.Success -> {
                    binding.recycler.visibility = View.VISIBLE
                    binding.pb.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE

                    val adapter = ActivitiesAdapter(cUser!!)
                    binding.recycler.apply {
                        this.adapter = adapter
                        layoutManager = LinearLayoutManager(requireContext())
                        addItemDecoration(NotificationItemDecoration())
                    }

                    adapter.submitList(it.data)
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()

        viewModel.getUserProfile()
    }

}