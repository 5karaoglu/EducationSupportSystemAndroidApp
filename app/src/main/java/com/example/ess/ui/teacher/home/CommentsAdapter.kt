package com.example.ess.ui.teacher.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.R
import com.example.ess.databinding.CommentsSingleItemBinding
import com.example.ess.databinding.HomeSingleItemBinding
import com.example.ess.databinding.SubcommentSingleItemBinding
import com.example.ess.model.Comment
import com.squareup.picasso.Picasso

class CommentsAdapter (
        private val listener: OnItemClickListener
): ListAdapter<Comment, CommentsAdapter.CommentsViewHolder>(CommentsComparator()) {


    class CommentsViewHolder(private val binding: CommentsSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(comment: Comment, listener: OnItemClickListener){
            Log.d("debug", "bind: binded")
            binding.tvName.text = comment.name
            binding.tvComment.text = comment.comment
            binding.tvSeeReplies.text = "See all ${comment.subCommentsCount} comments"

            if (comment.imageUrl.isNotEmpty()){
                Picasso.get()
                        .load(comment.imageUrl)
                        .fit().centerInside()
                        .into(binding.iv)
            }
            binding.tvSeeReplies.setOnClickListener {
                listener.onTvClicked(comment)
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder( CommentsSingleItemBinding.inflate(LayoutInflater.from(parent.context),
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
        fun onTvClicked(comment: Comment)

    }


}