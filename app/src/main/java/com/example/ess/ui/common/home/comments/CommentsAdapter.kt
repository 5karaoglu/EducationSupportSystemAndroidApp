package com.example.ess.ui.common.home.comments

import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.databinding.CommentsSingleItemBinding
import com.example.ess.model.Comment
import com.example.ess.util.Functions
import com.squareup.picasso.Picasso

class CommentsAdapter(
        private val listener: OnItemClickListener
) : ListAdapter<Comment, CommentsAdapter.CommentsViewHolder>(CommentsComparator()) {


    class CommentsViewHolder(private val binding: CommentsSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment, listener: OnItemClickListener) {
            Log.d("debug", "bind: binded")
            binding.tvText.text = SpannableStringBuilder()
                    .bold { append(comment.name) }
                    .append("  ")
                    .append(comment.comment)
            binding.tvTime.text = Functions.tsToDate(comment.timestamp)
            /*binding.tvSeeReplies.text = "See all ${comment.subCommentsCount} comments"*/

            if (comment.imageUrl.isNotEmpty()) {
                Picasso.get()
                        .load(comment.imageUrl)
                        .fit().centerInside()
                        .into(binding.iv)
            }
            /*binding.tvSeeReplies.setOnClickListener {
                listener.onTvClicked(comment)
            }*/

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(CommentsSingleItemBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
    }


    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val currentCommentsItem = getItem(position)
        holder.bind(currentCommentsItem, listener)
    }

    class CommentsComparator() : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.comment == newItem.comment
        }

    }

    interface OnItemClickListener {
        fun onTvClicked(comment: Comment)

    }


}