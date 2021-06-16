package com.example.ess.ui.common.messages

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.databinding.ChatReceivedSingleBinding
import com.example.ess.model.Message

class ChatReceivedAdapter(
        private val listener: OnItemClickListener
) : ListAdapter<Message, ChatReceivedAdapter.ChatViewHolder>(ChatComparator()) {


    class ChatViewHolder(private val binding: ChatReceivedSingleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, listener: OnItemClickListener) {
            Log.d("debug", "bind: binded")
            binding.text.text = message.message
            binding.time.text = message.timestamp

            itemView.setOnClickListener {
                listener.onItemClicked(message)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ChatReceivedSingleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false))
    }


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentMessage = getItem(position)
        holder.bind(currentMessage, listener)
    }

    class ChatComparator() : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.message == newItem.message
        }

    }

    interface OnItemClickListener {
        fun onItemClicked(message: Message)
    }


}