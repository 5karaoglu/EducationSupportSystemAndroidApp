package com.example.ess.ui.common.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.databinding.SearchSingleItemBinding
import com.example.ess.model.User
import com.squareup.picasso.Picasso

class SearchAdapter(private val listener: OnItemClickListener) : ListAdapter<User, SearchAdapter.SearchViewHolder>(SearchComparator()) {

    class SearchViewHolder(private val binding: SearchSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User, listener: OnItemClickListener) {
            Log.d("SearchViewHolde", "bind: here ${user.name}")
            binding.tvUsername.text = user.name
            binding.tvEmail.text = user.email
            Picasso.get()
                    .load(user.imageUrl)
                    .fit().centerInside()
                    .into(binding.ivUser)

            itemView.setOnClickListener {
                listener.onItemClicked(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        Log.d("TAG", "onCreateViewHolder: its created")
        val view = SearchSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentNotification = getItem(position)
        holder.bind(currentNotification, listener)
    }

    class SearchComparator() : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return false
        }

    }

    interface OnItemClickListener {
        fun onItemClicked(user: User)
    }
}