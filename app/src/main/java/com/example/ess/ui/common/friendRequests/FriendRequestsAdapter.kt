package com.example.ess.ui.common.friendRequests

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.databinding.FriendRequestsSingleItemBinding
import com.example.ess.databinding.HomeSingleItemBinding
import com.example.ess.model.User
import com.squareup.picasso.Picasso

class FriendRequestsAdapter(
    private val listener: OnItemClickListener
): ListAdapter<User, FriendRequestsAdapter.FriendRequestsViewHolder>(FeedComparator()) {


    class FriendRequestsViewHolder(private val binding: FriendRequestsSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(user: User, listener: OnItemClickListener) {
            binding.apply {
                tvText.text = SpannableStringBuilder()
                    .bold { append(user.name) }
                    .append(" wants to be your friend.")
                Picasso.get()
                    .load(user.imageUrl)
                    .fit().centerInside()
                    .into(iv)
                approve.setOnClickListener {
                    listener.onApproveClicked(user)
                }
                deny.setOnClickListener {
                    listener.onDenyClicked(user)
                }
                iv.setOnClickListener {
                    listener.onIvClicked(user)
                }
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestsViewHolder {
        return FriendRequestsViewHolder( FriendRequestsSingleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false))
    }



    override fun onBindViewHolder(holder: FriendRequestsViewHolder, position: Int) {
        val currentFeedItem = getItem(position)
        holder.bind(currentFeedItem,listener)
    }
    class FeedComparator(): DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

    }
    interface OnItemClickListener{
        fun onApproveClicked(user: User)
        fun onDenyClicked(user: User)
        fun onIvClicked(user: User)
    }


}