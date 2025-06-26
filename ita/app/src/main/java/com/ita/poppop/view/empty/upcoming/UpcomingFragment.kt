package com.ita.poppop.view.empty.upcoming

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentHomeSearchBinding
import com.ita.poppop.databinding.FragmentUpcomingBinding
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.view.empty.search.holder.HomeSearchAdapter
import com.ita.poppop.view.empty.search.holder.HomeSearchItemDecoration
import com.ita.poppop.view.empty.upcoming.holder.UpcomingAdapter
import com.ita.poppop.view.empty.upcoming.holder.UpcomingItemDecoration


class UpcomingFragment: BaseFragment<FragmentUpcomingBinding>(R.layout.fragment_upcoming) {

    override fun initView() {
        setupWindowInsets()
        setupToolbar()
        setupUpcomingRecyclerView()


    }

    private fun setupToolbar() {
        binding.mtHomeSearch.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }

    private fun setupUpcomingRecyclerView(){
        val upcomingList = (0..9).toMutableList()
        val adapter = UpcomingAdapter(upcomingList)

        with(binding.rvUpcoming) {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            this.adapter = adapter
            addItemDecoration(UpcomingItemDecoration())

//            val itemTouchHelper = ItemTouchHelper(SwipeHelper())
//            itemTouchHelper.attachToRecyclerView(this)
        }
    }
}