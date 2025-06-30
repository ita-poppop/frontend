package com.ita.poppop.view.empty.info.review

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popup.ReviewRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoReviewBinding
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.view.main.home.InfoFragmentDirections

class InfoReviewFragment: BaseFragment<FragmentInfoReviewBinding>(R.layout.fragment_info_review) {

    private lateinit var infoReviewViewModel: InfoReviewViewModel

    private val infoReviewRVAdapter by lazy {
        InfoReviewRVAdapter()
    }

    override fun initView() {
        binding.apply {
            //infoReviewViewModel = ViewModelProvider(this@InfoReviewFragment).get(InfoReviewViewModel::class.java)
            val repository = ReviewRepositoryImpl(RetrofitClient.reviewApi)
            val factory = ViewModelFactory { InfoReviewViewModel(repository) }
            infoReviewViewModel = ViewModelProvider(this@InfoReviewFragment, factory)[InfoReviewViewModel::class.java]

            // 리뷰
            rvInfoReview.apply {
                infoReviewViewModel.getInfoReview()

                val layoutmanager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                layoutManager = layoutmanager
                adapter = infoReviewRVAdapter

                val dividerItemDecoration = DividerItemDecoration(context, layoutmanager.orientation)
                addItemDecoration(dividerItemDecoration)

                infoReviewViewModel.inforeviewList.observe(viewLifecycleOwner) { reviewList ->
                    infoReviewRVAdapter.submitList(reviewList.toList()) // List 변환 후 submitList 호출
                    //emptyStateLayout.root.run { if(response.isNullOrEmpty()) show() else hide()}
                }
            }

            infoReviewViewModel.getInfoReview()

            infoReviewRVAdapter.setInfoReviewItemClickListener(object : InfoReviewRVAdapter.InfoReviewItemClickListener{
                override fun onItemClick(position: Int) {
                    // 선택된 리뷰 객체 전달
                    val selectedReview = infoReviewRVAdapter.currentList[position]
                    val parentNavController = requireParentFragment().findNavController()
                    val action = InfoFragmentDirections.actionInfoFragmentToInfoReviewDetailFragment(selectedReview)
                    parentNavController.navigate(action)

                }
            })
        }
    }
}