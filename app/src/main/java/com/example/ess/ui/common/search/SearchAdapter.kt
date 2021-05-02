package com.example.ess.ui.common.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.R
import com.example.ess.databinding.FragmentSearchBinding
import com.example.ess.databinding.SearchSingleItemBinding
import com.example.ess.model.Notification
import com.example.ess.model.UserShort
import com.squareup.picasso.Picasso

class SearchAdapter(): ListAdapter<UserShort,SearchAdapter.SearchViewHolder>(SearchComparator()) {
    
    class SearchViewHolder(private val binding: SearchSingleItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(userShort: UserShort){
            Log.d("SearchViewHolde", "bind: here ${userShort.name}")
            binding.tvUsername.text = userShort.name
            Picasso.get()
                    .load(userShort.imageURL)
                    .fit().centerInside()
                    .into(binding.ivUser)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        Log.d("TAG", "onCreateViewHolder: its created")
        val view = SearchSingleItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentNotification = getItem(position)
        holder.bind(currentNotification)
    }
    class SearchComparator(): DiffUtil.ItemCallback<UserShort>(){
        override fun areItemsTheSame(oldItem: UserShort, newItem: UserShort): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: UserShort, newItem: UserShort): Boolean {
            return false
        }

    }
}