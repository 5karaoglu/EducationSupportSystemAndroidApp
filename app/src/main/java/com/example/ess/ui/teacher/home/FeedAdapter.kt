package com.example.ess.ui.teacher.home

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.ess.R
import com.example.ess.databinding.HomeSingleItemBinding
import com.example.ess.model.FeedItem
import com.example.ess.util.Functions
import com.squareup.picasso.Picasso

class FeedAdapter (
        private val listener: OnItemClickListener
): ListAdapter<FeedItem, FeedAdapter.FeedViewHolder>(FeedComparator()) {


    class FeedViewHolder(private val binding: HomeSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(feedItem: FeedItem, listener: OnItemClickListener){
            Log.d("debug", "bind: binded")
            binding.tvName.text = feedItem.publishedBy
            binding.tvLastDeliveryTime.text = "Deadline: " + Functions.tsToDate(feedItem.deadline)
            binding.btnAttachedFile.text = feedItem.fileName
            binding.tvDescription.text = feedItem.description
            binding.tvTitle.text = feedItem.title
            binding.tvSubmits.text = feedItem.submitsCount
            binding.tvComments.text = feedItem.commentsCount
            if (feedItem.publisherImageUrl.isNotEmpty()){
                Picasso.get()
                        .load(feedItem.publisherImageUrl)
                        .error(R.drawable.ic_outline_person_24)
                        .fit().centerInside()
                        .into(binding.ivUser)
            }

            itemView.setOnClickListener {
                listener.onViewClicked(feedItem)
            }
            binding.ibComment.setOnClickListener {
                listener.onCommentsClicked(feedItem)
            }
            binding.ibPublish.setOnClickListener {
                listener.onSubmitsClicked(feedItem)
            }
            binding.ivUser.setOnClickListener {
                listener.onIvClicked(feedItem)
            }
        }
    }

    

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder( HomeSingleItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,false))}



    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val currentFeedItem = getItem(position)
        holder.bind(currentFeedItem,listener)
    }
    class FeedComparator(): DiffUtil.ItemCallback<FeedItem>(){
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem.title == newItem.title
        }

    }
    interface OnItemClickListener{
        fun onViewClicked(feedItem: FeedItem)
        fun onCommentsClicked(feedItem: FeedItem)
        fun onSubmitsClicked(feedItem: FeedItem)
        fun onIvClicked(feedItem: FeedItem)

    }


}