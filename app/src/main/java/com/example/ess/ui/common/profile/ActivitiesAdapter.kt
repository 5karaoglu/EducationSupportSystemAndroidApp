package com.example.ess.ui.common.profile

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.databinding.ActivitiesSingleItemBinding
import com.example.ess.model.ActivityItem
import com.example.ess.model.UserProfile
import com.example.ess.util.Functions

class ActivitiesAdapter(
        private val user: UserProfile
) :
        ListAdapter<ActivityItem, ActivitiesAdapter.ActivitiesViewHolder>(ActivitiesComparator()) {


    class ActivitiesViewHolder(private val binding: ActivitiesSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(activityItem: ActivityItem, user: UserProfile) {
            Log.d("debug", "bind: binded")
            binding.tvText.text = Functions.activityType(activityItem, user)
            binding.tvTime.text = Functions.tsToDate(activityItem.timestamp)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        return ActivitiesViewHolder(ActivitiesSingleItemBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
    }


    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val currentActivityItem = getItem(position)
        holder.bind(currentActivityItem, user)
    }

    class ActivitiesComparator() : DiffUtil.ItemCallback<ActivityItem>() {
        override fun areItemsTheSame(oldItem: ActivityItem, newItem: ActivityItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ActivityItem, newItem: ActivityItem): Boolean {
            return oldItem.job == newItem.job
        }

    }
}