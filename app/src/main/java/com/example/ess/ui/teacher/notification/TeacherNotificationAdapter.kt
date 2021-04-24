package com.example.ess.ui.teacher.notification

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
import com.example.ess.model.Notification
import com.squareup.picasso.Picasso

class TeacherNotificationAdapter(): ListAdapter<Notification, TeacherNotificationAdapter.TeacherViewHolder>(TeacherComparator()) {


    class TeacherViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val TAG ="TeacherNotificationAdapter"
        val tvChannelName = itemView.findViewById<TextView>(R.id.channelName)
        val tvTitle = itemView.findViewById<TextView>(R.id.notificationTitle)
        val tvDes = itemView.findViewById<TextView>(R.id.tvDes)
        val ivDes = itemView.findViewById<ImageView>(R.id.ivDes)

        fun bind(notification: Notification){
            Log.d(TAG, "bind: here ${notification.title}")
            tvChannelName.text = notification.title
            tvTitle.text = notification.title
            tvDes.text = notification.descripton
            Picasso.get()
                .load(R.drawable.ic_outline_person_24)
                .fit().centerInside()
                .into(ivDes)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        Log.d("TAG", "onCreateViewHolder: its created")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_single_item,parent,false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val currentNotification = getItem(position)
        holder.bind(currentNotification)
    }
    class TeacherComparator(): DiffUtil.ItemCallback<Notification>(){
        private val TAG ="TeacherNotificationAdapter"
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