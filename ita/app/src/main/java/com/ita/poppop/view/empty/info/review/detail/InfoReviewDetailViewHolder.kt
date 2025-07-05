package com.ita.poppop.view.empty.info.review.detail

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.databinding.FragmentInfoReviewDetailBinding
import com.ita.poppop.view.empty.info.review.InfoReviewRVItem
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVAdapter

class InfoReviewDetailViewHolder(
    private val binding: FragmentInfoReviewDetailBinding,
    private val imageAdapter: InfoReviewImageRVAdapter
) {
    fun bind(review: InfoReviewRVItem, viewModel: InfoReviewDetailViewModel) {
        with(binding) {
            tvReviewDetailContent.text = review.content
            tvReviewDetailUsername.text = review.username
            tvReviewDetailTime.text = review.time
            tvReviewDetailHeart.text = review.hearts.toString()
            tvReviewDetailComment.text = review.comments.toString()

            imageAdapter.submitList(review.reviewImage)
            if (review.reviewImage.isNullOrEmpty()) {
                rvReviewDetailImage.visibility = View.GONE
            } else {
                rvReviewDetailImage.visibility = View.VISIBLE
                rvReviewDetailImage.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = imageAdapter
                }
                imageAdapter.submitList(review.reviewImage)
            }

            Glide.with(ivReviewDetailProfile.context)
                .load(review.profileImage)
                .placeholder(R.drawable._profile_load_icon)
                .error(R.drawable._profile_load_icon)
                .circleCrop()
                .into(ivReviewDetailProfile)

            // 기존 개수 전달
            viewModel.firstHeartCount(review.hearts)
        }
    }
}