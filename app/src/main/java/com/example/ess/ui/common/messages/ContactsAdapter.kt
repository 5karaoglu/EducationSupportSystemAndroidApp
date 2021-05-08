package com.example.ess.ui.common.messages

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.R
import com.example.ess.databinding.MessagesSingleItemBinding
import com.example.ess.model.Contact
import com.squareup.picasso.Picasso

class ContactsAdapter(
        private val listener: OnItemClickListener
): ListAdapter<Contact, ContactsAdapter.ContactsViewHolder>(ContactsComparator()) {


    class ContactsViewHolder(private val binding: MessagesSingleItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(contact: Contact,listener: OnItemClickListener){
            binding.tvName.text = contact.name
            binding.tvLastMessage.text = contact.lastMessage
            Picasso.get()
                    .load(contact.imageUrl)
                    .centerInside().fit()
                    .into(binding.ivUser)

            itemView.setOnClickListener {
                listener.onItemClicked(contact)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        Log.d("TAG", "onCreateViewHolder: its created")
        val view = MessagesSingleItemBinding.inflate(LayoutInflater.from(parent.context),
        parent,false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val currentContact = getItem(position)
        holder.bind(currentContact,listener)
    }
    class ContactsComparator(): DiffUtil.ItemCallback<Contact>(){
        private val TAG ="ContactsContactAdapter"
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            Log.d(TAG, "areItemsTheSame: ${oldItem == newItem}")
            return false
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            Log.d(TAG, "areContentsTheSame: sup")
            return false
        }

    }
    interface OnItemClickListener{
        fun onItemClicked(contact: Contact)
    }


}