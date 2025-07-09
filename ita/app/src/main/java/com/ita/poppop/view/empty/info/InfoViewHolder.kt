package com.ita.poppop.view.empty.info

import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.data.remote.dto.popups.PopupDetailData
import com.ita.poppop.databinding.FragmentInfoBinding

class InfoViewHolder(
    private val binding: FragmentInfoBinding
) {

    fun bind(info: PopupDetailData, viewModel: InfoViewModel) {
        binding.apply {

            val newDate = info.date
                .replace("-", ".")
                .replace("~", "-")

            tvInfoTopTitle.text = info.title
            tvInfoTitle.text = info.title
            tvInfoLocation.text = info.location.substringBefore("\n").trim()
            tvInfoDate.text = newDate

            Glide.with(ivInfoImage.context)
                .load(info.imageUrl)
                .placeholder(R.drawable.app_logo)
                .centerCrop()
                .into(ivInfoImage)
        }
    }
}
