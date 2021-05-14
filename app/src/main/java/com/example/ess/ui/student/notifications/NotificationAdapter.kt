package com.example.ess.ui.student.notifications

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
import com.example.ess.databinding.FragmentStudentNotificationsBinding
import com.example.ess.databinding.NotificationSingleItemBinding
import com.example.ess.model.Notification
import com.example.ess.ui.teacher.notification.TeacherNotificationAdapter
import com.squareup.picasso.Picasso

class NotificationAdapter (): ListAdapter<Notification, NotificationAdapter.NotificationsViewHolder>(Comparator()) {


    class NotificationsViewHolder(private val binding: NotificationSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        private val TAG ="NotificationAdapter"

        fun bind(notification: Notification){
            binding.notificationTitle = notification.title
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