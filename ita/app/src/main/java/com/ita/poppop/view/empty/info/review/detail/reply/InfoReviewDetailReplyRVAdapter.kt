package com.ita.poppop.view.empty.info.review.detail.reply

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.databinding.ItemInfoReviewDetailReplyBinding
import com.ita.poppop.view.empty.info.review.InfoReviewDetailReplyRVItem
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentDeleteBottomSheet
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVAdapter.InfoReviewCommentItemClickListener

class InfoReviewDetailReplyRVAdapter: ListAdapter<InfoReviewDetailReplyRVItem, InfoReviewDetailReplyRVAdapter.InfoReviewDetailReplyViewHolder>(
    InfoReviewDetailReplyDiffutillCallback()
) {

    interface InfoReviewDetailReplyItemClickListener{
        fun onDotClick(position: Int)
    }

    private lateinit var infoReviewDetailReplyItemClickListener : InfoReviewDetailReplyItemClickListener

    fun setInfoReviewDetailReplyItemClickListener(itemClickListener: InfoReviewDetailReplyItemClickListener){
        infoReviewDetailReplyItemClickListener = itemClickListener
    }

    class InfoReviewDetailReplyViewHolder(val binding: ItemInfoReviewDetailReplyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoReviewDetailReplyRVItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.profileImage)
                    .into(ivReplyProfile)
                tvReplyUsername.text = item.username
                tvReplyTime.text = item.time
                tvReplyContent.text = item.reply
            }
        }
    }

    class InfoReviewDetailReplyDiffutillCallback : DiffUtil.ItemCallback<InfoReviewDetailReplyRVItem>() {
        override fun areItemsTheSame(oldItem: InfoReviewDetailReplyRVItem, newItem: InfoReviewDetailReplyRVItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: InfoReviewDetailReplyRVItem,
            newItem: InfoReviewDetailReplyRVItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoReviewDetailReplyViewHolder {
        val binding = ItemInfoReviewDetailReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoReviewDetailReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoReviewDetailReplyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.binding.ivInfoReviewReplyDot.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                infoReviewDetailReplyItemClickListener.onDotClick(pos)
            }
        }
    }
}