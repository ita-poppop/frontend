package com.ita.poppop.view.main.home.direction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ita.poppop.R
import com.ita.poppop.databinding.ItemHomeDirectionLayoutBinding
import com.naver.maps.geometry.LatLng


class HomeDirectionAdapter(
    private val onClick: (String,Float,Float) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val directionList = listOf(
        mapOf(
            "name" to "성수",
            "latitude" to 37.5447,
            "longitude" to 127.0557,
            "image" to R.drawable.img_seongsu
        ),
        mapOf(
            "name" to "여의도",
            "latitude" to 37.5215,
            "longitude" to 126.9244,
            "image" to R.drawable.img_yeouido
        ),
        mapOf(
            "name" to "홍대",
            "latitude" to 37.5568,
            "longitude" to 126.9236,
            "image" to R.drawable.img_hongdae
        ),
        mapOf(
            "name" to "강남",
            "latitude" to 37.4979,
            "longitude" to 127.0276,
            "image" to R.drawable.img_gangnam
        ),
        mapOf(
            "name" to "잠실",
            "latitude" to 37.5133,
            "longitude" to 127.1000,
            "image" to R.drawable.img_jamsil
        ),
        mapOf(
            "name" to "용산",
            "latitude" to 37.5299,
            "longitude" to 126.9649,
            "image" to R.drawable.img_yongsan
        )
    )

    inner class HomeDirectionViewHolder(
        private val binding: ItemHomeDirectionLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Map<String, Any>) = with(binding) {
            tvDirectionTitle.text = item["name"] as String
            tvDirectionThumbnail.setImageResource(item["image"] as Int)
            binding.root.setOnClickListener {
                onClick(
                    item["name"] as String,
                    (item["latitude"] as Double).toFloat(),
                    (item["longitude"] as Double).toFloat()
                )
            }
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return HomeDirectionViewHolder(ItemHomeDirectionLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeDirectionViewHolder).bind(getItem(position))
    }


    // 아이템 반환 메서드
    private fun getItem(position: Int): Map<String, Any> {
        return directionList[position]
    }

    // 아이템 개수 반환 메서
    override fun getItemCount(): Int = directionList.size

    // 아이템 고유 ID 반환 메서드
    override fun getItemId(position: Int): Long {
        return if (position in directionList.indices) {
            directionList[position].hashCode().toLong()
        } else {
            -1L // 아이디를 찾지 못했을 때 반환되는 기본값
        }

    }


}