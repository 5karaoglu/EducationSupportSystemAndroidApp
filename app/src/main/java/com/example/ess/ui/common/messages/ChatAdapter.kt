package com.example.ess.ui.common.messages

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.ess.databinding.ChatReceivedSingleBinding
import com.example.ess.databinding.ChatSentSingleItemBinding
import com.example.ess.model.Message
import com.example.ess.util.Functions

class ChatAdapter (
    private val myUid:String,
    private val receiverUid: String,
    private val listener: OnItemClickListener
): ListAdapter<Message,ChatAdapter.ChatViewHolder>(ChatComparator()) {


    class ChatViewHolder(private val binding: ViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message,viewType: Int, listener: OnItemClickListener){
            Log.d("debug", "bind: binded")
            if (viewType == 0){
                (binding as ChatSentSingleItemBinding).text.text = message.message
                binding.time.text = Functions.tsToHm(message.timestamp)
                var layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.gravity = Gravity.END
                itemView.layoutParams = layoutParams
            }else{
                (binding as ChatReceivedSingleBinding).text.text = message.message
                binding.time.text = Functions.tsToHm(message.timestamp)
                var layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.gravity = Gravity.END
                itemView.layoutParams = layoutParams
            }
            
            itemView.setOnClickListener {
                listener.onItemClicked(message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentItem = getItem(position)
        return if (currentItem.whoSent == myUid){
            0
        }else{
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == 0){
            ChatViewHolder(ChatSentSingleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,false))
        }else{
            ChatViewHolder(ChatReceivedSingleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,false))
        }}



    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentMessage = getItem(position)
        val viewType = getItemViewType(position)
        holder.bind(currentMessage,viewType,listener)
    }
    class ChatComparator(): DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return false
        }

    }
    interface OnItemClickListener{
        fun onItemClicked(message: Message)
    }


}