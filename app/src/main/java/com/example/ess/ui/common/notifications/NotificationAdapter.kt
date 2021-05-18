package com.example.ess.ui.common.notifications

import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.italic
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.databinding.NotificationSingleItemBinding
import com.example.ess.model.Notification
import com.example.ess.util.Functions
import com.squareup.picasso.Picasso

class NotificationsAdapter (): ListAdapter<Notification, NotificationsAdapter.NotificationsViewHolder>(Comparator()) {


    class NotificationsViewHolder(private val binding: NotificationSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        private val TAG ="NotificationAdapter"

        fun bind(notification: Notification){
            binding.notificationTitle.text = notification.title
            binding.notificationDes.text = notification.description
            Picasso.get()
                    .load(notification.imageUrl)
                    .fit().centerInside()
                    .into(binding.iv)
            binding.tvInfo.text = SpannableStringBuilder()
                    .bold { append(notification.name) }
                    .append(" to ")
                    .append(notification.className)
                    .append(" at ")
                    .italic { append(Functions.tsToDate(notification.timestamp)) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        Log.d("TAG", "onCreateViewHolder: its created")
        val view = NotificationSingleItemBinding.inflate(LayoutInflater.from(parent.context),
        parent,false)
        return NotificationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val currentNotification = getItem(position)
        holder.bind(currentNotification)
    }
    class Comparator(): DiffUtil.ItemCallback<Notification>(){
        private val TAG ="NotificationAdapter"
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            Log.d(TAG, "areItemsTheSame: ${oldItem == newItem}")
            return false
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            Log.d(TAG, "areContentsTheSame: sup")
            return false
        }

    }


}