package com.ita.poppop.view.empty.info.review.comment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ita.poppop.databinding.ItemInfoReviewBinding
import com.ita.poppop.databinding.ItemInfoReviewCommentBinding
import com.ita.poppop.view.empty.info.review.InfoReviewRVAdapter
import com.ita.poppop.view.empty.info.review.InfoReviewRVItem
import com.ita.poppop.view.empty.info.review.detail.InfoReviewDeleteBottomSheet
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVAdapter
import com.ita.poppop.view.main.home.InfoFragmentDirections

class InfoReviewCommentRVAdapter: ListAdapter<InfoReviewCommentRVItem, InfoReviewCommentRVAdapter.InfoReviewCommentViewHolder>(
    InfoReviewCommentDiffutillCallback()
) {

    interface InfoReviewCommentItemClickListener{
        fun onArrowClick(position: Int)
        fun onDotClick(position: Int)
    }
    private lateinit var infoReviewCommentItemClickListener : InfoReviewCommentItemClickListener

    fun setInfoReviewCommentItemClickListener(itemClickListener: InfoReviewCommentItemClickListener){
        infoReviewCommentItemClickListener = itemClickListener
    }

    class InfoReviewCommentViewHolder(val binding: ItemInfoReviewCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoReviewCommentRVItem) {
            binding.apply {
                ivCommentProfile.setImageResource(item.profileImage)
                tvCommentUsername.text = item.username
                tvCommentTime.text = item.time
                tvCommentContent.text = item.content
                tvCommentReplyNum.text = item.reply.toString()

                // 답글 개수 0일시, 레이아웃 숨김 처리
                if (item.reply == null || item.reply == 0) {
                    //clInfoReviewCommentReply.visibility = View.GONE
                    tvCommentReply.text = "답글 쓰기"
                    tvCommentReplyL.visibility = View.GONE
                    tvCommentReplyNum.visibility = View.GONE
                    view1.visibility = View.GONE
                    view2.visibility = View.GONE
                } else {
                    clInfoReviewCommentReply.visibility = View.VISIBLE
                    view1.visibility = View.VISIBLE
                }

            }
        }
    }

    class InfoReviewCommentDiffutillCallback : DiffUtil.ItemCallback<InfoReviewCommentRVItem>() {
        override fun areItemsTheSame(oldItem: InfoReviewCommentRVItem, newItem: InfoReviewCommentRVItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: InfoReviewCommentRVItem,
            newItem: InfoReviewCommentRVItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoReviewCommentViewHolder {
        val binding = ItemInfoReviewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoReviewCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoReviewCommentViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.binding.clInfoReviewCommentReply.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                infoReviewCommentItemClickListener.onArrowClick(pos)
            }
        }
        holder.binding.ivInfoReviewCommentDot.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                infoReviewCommentItemClickListener.onDotClick(pos)
            }
        }
    }
}