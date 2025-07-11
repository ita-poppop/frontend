package com.ita.poppop.view.main.profile.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.data.remote.dto.profile.ReviewItem
import com.ita.poppop.databinding.ItemProfileReviewLayoutBinding


class ProfileReviewAdapter(
    private val onClick: (Int) -> Unit,
    private var items : List<ReviewItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class ProfileReviewViewHolder(
        private val binding: ItemProfileReviewLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReviewItem) {
            binding.root.setOnClickListener {
                onClick(item.popupId)
            }


            Glide.with(binding.root)
                .load(item.popupImageUrl)
                .centerCrop()
                .into(binding.ivUserReviewPoster)

            binding.tvUserReviewTitle.text = item.popupTitle
            binding.tvUserReviewDate.text = item.startDate + "-" + item.endDate

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return ProfileReviewViewHolder(ItemProfileReviewLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProfileReviewViewHolder).bind(items[position])
    }


    // 아이템 반환 메서드
    private fun getItem(position: Int): ReviewItem {
        return items[position]
    }

    // 아이템 개수 반환 메서
    override fun getItemCount(): Int = items.size

    // 아이템 고유 ID 반환 메서드
    override fun getItemId(position: Int): Long {
        return if (position in items.indices) {
            items[position].hashCode().toLong()
        } else {
            -1L // 아이디를 찾지 못했을 때 반환되는 기본값
        }

    }


}