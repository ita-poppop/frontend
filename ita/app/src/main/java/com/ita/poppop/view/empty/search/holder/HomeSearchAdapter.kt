package com.ita.poppop.view.empty.search.holder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.databinding.ItemHomeSearchLayoutBinding


class HomeSearchAdapter(
    private val onAddClick: (item : SearchData) -> Unit,
    private var items : List<SearchData>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class HomeSearchViewHolder(
        private val binding: ItemHomeSearchLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : SearchData) {
            binding.root.setOnClickListener {
                onAddClick(item)
            }
            Glide.with(binding.root)
                .load(item.image)
                .centerCrop()
                .into(binding.ivSearchPoster)

            binding.tvSearchTitle.text = item.title
            binding.tvSearchLocation.text = item.location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return HomeSearchViewHolder(ItemHomeSearchLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeSearchViewHolder).bind(items[position])
    }


    // 아이템 반환 메서드
    private fun getItem(position: Int): SearchData {
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