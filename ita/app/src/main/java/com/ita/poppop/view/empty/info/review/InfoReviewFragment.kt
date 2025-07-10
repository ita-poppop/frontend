package com.ita.poppop.view.empty.info.review

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.popups.PopupDetailData
import com.ita.poppop.data.remote.repository.popups.ReviewRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoReviewBinding
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.view.empty.info.InfoFragmentDirections
import com.ita.poppop.viewmodel.MainAViewModel

class InfoReviewFragment: BaseFragment<FragmentInfoReviewBinding>(R.layout.fragment_info_review) {

    private lateinit var infoReviewViewModel: InfoReviewViewModel

    val mainViewModel: MainAViewModel by activityViewModels()

    private val infoReviewRVAdapter by lazy {
        InfoReviewRVAdapter()
    }

    override fun initView() {
        binding.apply {

            val popupId = arguments?.getInt("popupId") ?: 0

            //infoReviewViewModel = ViewModelProvider(this@InfoReviewFragment).get(InfoReviewViewModel::class.java)
            val repository = ReviewRepositoryImpl(RetrofitClient.reviewApi)
            val factory = ViewModelFactory { InfoReviewViewModel(mainViewModel.tokenPair.value.first.toString(),repository) }
            infoReviewViewModel = ViewModelProvider(this@InfoReviewFragment, factory)[InfoReviewViewModel::class.java]

            // 리뷰
            rvInfoReview.apply {
                infoReviewViewModel.getInfoReview(popupId)

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

            infoReviewViewModel.getInfoReview(popupId)

            infoReviewRVAdapter.setInfoReviewItemClickListener(object : InfoReviewRVAdapter.InfoReviewItemClickListener{
                override fun onItemClick(position: Int) {
                    val popupId = arguments?.getInt("popupId") ?: 0
                    val popupItem = arguments?.getParcelable<PopupDetailData>("popupItem")
                    // 선택된 리뷰 객체 전달
                    val selectedReview = infoReviewRVAdapter.currentList[position]
                    val parentNavController = requireParentFragment().findNavController()
                    val action = InfoFragmentDirections.actionInfoFragmentToInfoReviewDetailFragment(
                        popupId = popupId,
                        review = selectedReview,
                        popupItem = popupItem
                    )
                    parentNavController.navigate(action)

                }
            })
        }
    }
}