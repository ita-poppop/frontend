package com.ita.poppop.view.empty.info.review.detail.reply

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.databinding.FragmentInfoReviewDetailReplyBinding
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVItem

class InfoReviewCommentDetailViewHolder(
    private val binding: FragmentInfoReviewDetailReplyBinding
) {
    fun bind(comment: InfoReviewCommentRVItem) {
        with(binding) {
            tvReviewCommentContent.text = comment.content
            tvReviewCommentUsername.text = comment.username
            tvReviewCommentTime.text = comment.time

            Glide.with(ivReviewCommentProfile.context)
                .load(comment.profileImage)
                .placeholder(R.drawable._profile_load_icon)
                .error(R.drawable._profile_load_icon)
                .circleCrop()
                .into(ivReviewCommentProfile)
        }
    }
}