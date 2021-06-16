package com.example.ess.ui.common.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ess.R
import com.example.ess.databinding.FragmentChatBinding
import com.example.ess.model.Contact
import com.example.ess.model.Message
import com.example.ess.ui.common.CommonViewModel
import com.example.ess.util.DataState
import com.example.ess.util.NotificationItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class ChatFragment : Fragment(), ChatAdapter.OnItemClickListener {

    private val TAG = "ChatFragment"
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommonViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private var myUid: String? = null
    private var receiverUid: String? = null
    private var chatId: String? = null
    private val messageList = mutableListOf<Message>()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        var bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        if (bottomBar == null) {
            bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavStudent)
        }
        bottomBar.visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        var bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        if (bottomBar == null) {
            bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavStudent)
        }
        bottomBar.visibility = View.VISIBLE
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProfile(args.contact)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ibSend.setOnClickListener {
            viewModel.sendMessage(args.contact.chatId, binding.etMessage.text.toString())
            binding.etMessage.text.clear()
        }
        val adapter = ChatAdapter(args.contact.myUid, args.contact.receiverUid, this)
        binding.recyclerMessages.adapter = adapter
        binding.recyclerMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMessages.addItemDecoration(NotificationItemDecoration())

        viewModel.getMessages(args.contact.chatId)

        viewModel.messagesState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    Log.d(TAG, "onViewCreated: Loading")
                    binding.pb.visibility = View.VISIBLE
                }
                is DataState.Error -> Toast.makeText(requireContext(), "Error ! ${it.throwable.message}", Toast.LENGTH_SHORT)
                        .show()
                is DataState.Success -> {
                    binding.pb.visibility = View.GONE
                    adapter.submitList(it.data)
                    binding.recyclerMessages.smoothScrollToPosition(
                            it.data.size - 1
                    )
                    /*viewModel.updateLastMessage(args.contact, it.data[it.data.size-1])*/
                }
            }
        }
    }

    private fun initProfile(contact: Contact) {
        Picasso.get()
                .load(contact.imageUrl)
                .fit().centerInside()
                .into(binding.ivUser)
        binding.tvUsername.text = contact.name
    }

    override fun onItemClicked(message: Message) {

    }
}