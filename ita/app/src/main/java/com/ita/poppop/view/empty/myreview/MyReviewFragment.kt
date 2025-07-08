package com.ita.poppop.view.empty.myreview

import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentMyReviewBinding
import com.ita.poppop.view.empty.myreview.holder.MyReviewAdapter
import com.ita.poppop.view.empty.myreview.holder.MyReviewItemDecoration


class MyReviewFragment : BaseFragment<FragmentMyReviewBinding>(
    R.layout.fragment_my_review
){

    override fun initView() {
        setupWindowInsets()
        setupToolbar()
        setupMyReviewRecycler()
    }

    private fun setupMyReviewRecycler() = with(binding.rvMyReview) {

        //fetchTrends()
        val myReviewList = mutableListOf(1, 2, 3, 4, 5, 6)
        adapter = MyReviewAdapter(myReviewList)
        layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        addItemDecoration(MyReviewItemDecoration())
    }


    private fun setupToolbar() {
        binding.mtMyReview.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }










}