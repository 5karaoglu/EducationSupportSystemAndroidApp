package com.example.ess.ui.teacher.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.R
import com.example.ess.databinding.HomeSingleItemBinding
import com.example.ess.databinding.SubmitsSingleItemBinding
import com.example.ess.model.Submit
import com.example.ess.util.Functions
import com.squareup.picasso.Picasso

class SubmitsAdapter(
        private val listener: OnItemClickListener
): ListAdapter<Submit, SubmitsAdapter.SubmitViewHolder>(SubmitComparator()) {


    class SubmitViewHolder(private val binding: SubmitsSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(feedItem: Submit, listener: OnItemClickListener){
            Log.d("debug", "bind: binded")

           
            if (feedItem.imageUrl.isNotEmpty()){
                Picasso.get()
                        .load(feedItem.imageUrl)
                        .error(R.drawable.ic_outline_person_24)
                        .fit().centerInside()
                        .into(binding.iv)
            }

            itemView.setOnClickListener {
                listener.onViewClicked(feedItem)
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmitViewHolder {
        return SubmitViewHolder( SubmitsSingleItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,false))}



    override fun onBindViewHolder(holder: SubmitViewHolder, position: Int) {
        val currentSubmit = getItem(position)
        holder.bind(currentSubmit,listener)
    }
    class SubmitComparator(): DiffUtil.ItemCallback<Submit>(){
        override fun areItemsTheSame(oldItem: Submit, newItem: Submit): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Submit, newItem: Submit): Boolean {
            return oldItem.uid == newItem.uid
        }

    }
    interface OnItemClickListener{
        fun onViewClicked(feedItem: Submit)
    }


}