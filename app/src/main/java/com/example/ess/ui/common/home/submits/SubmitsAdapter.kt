package com.example.ess.ui.common.home.submits

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.italic
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.R
import com.example.ess.databinding.SubmitsSingleItemBinding
import com.example.ess.model.Submit
import com.example.ess.util.Functions
import com.squareup.picasso.Picasso

class SubmitsAdapter(
        private val listener: OnItemClickListener
) : ListAdapter<Submit, SubmitsAdapter.SubmitViewHolder>(SubmitComparator()) {


    class SubmitViewHolder(private val binding: SubmitsSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        /*val linkblue = ResourcesCompat.getColor(Resources.getSystem(),R.color.link_blue,null)*/
        @SuppressLint("SetTextI18n")
        fun bind(submit: Submit, listener: OnItemClickListener) {
            Log.d("debug", "bind: binded")
            /*val sourceString = "<b>" + submit.name + "</b>" + " submitted " + submit.fileName + " at " +Functions.tsToDate(submit.timestamp)*/
            val s = SpannableStringBuilder()
                    .bold { append(submit.name) }
                    .append(" submitted ")
                    .italic { append(submit.fileName) }
                    .append(" at ")
                    .append(Functions.tsToDate(submit.timestamp))
            binding.tvName.text = s

            if (submit.imageUrl.isNotEmpty()) {
                Picasso.get()
                        .load(submit.imageUrl)
                        .error(R.drawable.ic_outline_person_24)
                        .fit().centerInside()
                        .into(binding.iv)
            }

            itemView.setOnClickListener {
                listener.onViewClicked(submit)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmitViewHolder {
        return SubmitViewHolder(SubmitsSingleItemBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
    }


    override fun onBindViewHolder(holder: SubmitViewHolder, position: Int) {
        val currentSubmit = getItem(position)
        holder.bind(currentSubmit, listener)
    }

    class SubmitComparator() : DiffUtil.ItemCallback<Submit>() {
        override fun areItemsTheSame(oldItem: Submit, newItem: Submit): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Submit, newItem: Submit): Boolean {
            return oldItem.uid == newItem.uid
        }

    }

    interface OnItemClickListener {
        fun onViewClicked(submit: Submit)
    }


}