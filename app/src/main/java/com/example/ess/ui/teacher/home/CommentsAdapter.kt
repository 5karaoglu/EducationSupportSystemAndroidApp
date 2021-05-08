package com.example.ess.ui.teacher.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.databinding.HomeSingleItemBinding
import com.example.ess.model.Comment
import com.squareup.picasso.Picasso

class CommentsAdapter (
        private val listener: OnItemClickListener
): ListAdapter<Comment, CommentsAdapter.CommentsViewHolder>(CommentsComparator()) {


    class CommentsViewHolder(private val binding: HomeSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(comment: Comment, listener: OnItemClickListener){
            Log.d("debug", "bind: binded")

            Picasso.get()
                    .load(comment.imageUrl)
                    .fit().centerInside()
                    .into(binding.ivUser)

            itemView.setOnClickListener {
                listener.onViewClicked(comment)
            }
            binding.ibComment.setOnClickListener {
                listener.onCommentsClicked(comment)
            }
            binding.ibPublish.setOnClickListener {
                listener.onSubscriptionsClicked(comment)
            }
            binding.ivUser.setOnClickListener {
                listener.onIvClicked(comment)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder( HomeSingleItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,false))}



    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val currentCommentsItem = getItem(position)
        holder.bind(currentCommentsItem,listener)
    }
    class CommentsComparator(): DiffUtil.ItemCallback<Comment>(){
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.comment == newItem.comment
        }

    }
    interface OnItemClickListener{
        fun onViewClicked(comment: Comment)
        fun onCommentsClicked(comment: Comment)
        fun onSubscriptionsClicked(comment: Comment)
        fun onIvClicked(comment: Comment)

    }


}