package com.ita.poppop.view.empty.info.review.detail

import android.graphics.Rect
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popup.ReviewRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoReviewDetailBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentDeleteBottomSheet
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVAdapter
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentViewModel
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVAdapter

class InfoReviewDetailFragment : BaseFragment<FragmentInfoReviewDetailBinding>(R.layout.fragment_info_review_detail){

    private val infoReviewDetailArgs: InfoReviewDetailFragmentArgs by navArgs()

    private lateinit var infoReviewDetailViewModel: InfoReviewDetailViewModel
    private lateinit var infoReviewDetailViewHolder: InfoReviewDetailViewHolder

    private val infoReviewImageRVAdapter by lazy {
        InfoReviewImageRVAdapter()
    }

    private lateinit var infoReviewCommentViewModel: InfoReviewCommentViewModel

    private val infoReviewCommentRVAdapter by lazy {
        InfoReviewCommentRVAdapter()
    }

    override fun initView() {
        setupWindowInsets()
        binding.apply {

            ivReviewDetailBack.setOnClickListener {
                //parentFragmentManager.popBackStack()
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("reviewTab", 1)
                findNavController().popBackStack()
            }


            //infoReviewDetailViewModel = ViewModelProvider(this@InfoReviewDetailFragment).get(InfoReviewDetailViewModel::class.java)
            //infoReviewDetailViewModel.getInfoReviewDetail(infoReviewDetailArgs.review.itemId)
            // 리뷰 상세
            val repository = ReviewRepositoryImpl(RetrofitClient.reviewApi)
            val factory = ViewModelFactory { InfoReviewDetailViewModel(repository) }
            infoReviewDetailViewModel = ViewModelProvider(this@InfoReviewDetailFragment, factory)[InfoReviewDetailViewModel::class.java]
            infoReviewDetailViewHolder = InfoReviewDetailViewHolder(binding, infoReviewImageRVAdapter)

            // 리뷰 상세 요청
            infoReviewDetailViewModel.getInfoReviewDetail(infoReviewDetailArgs.review.itemId)

            // 이미지 리사이클러뷰 설정
            rvReviewDetailImage.apply {
                adapter = infoReviewImageRVAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            infoReviewDetailViewModel.review.observe(viewLifecycleOwner) { review ->
                infoReviewDetailViewHolder.bind(review, infoReviewDetailViewModel)
            }

            // 하트 상태 변화
            infoReviewDetailViewModel.heartCount.observe(viewLifecycleOwner) { count ->
                tvReviewDetailHeart.text = count.toString()
            }

            infoReviewDetailViewModel.isHeartClicked.observe(viewLifecycleOwner) { clicked ->
                if (clicked) {
                    ivReviewDetailHeart.setImageResource(R.drawable.info_review_heart_icon_filled)
                } else {
                    ivReviewDetailHeart.setImageResource(R.drawable.info_review_heart_icon_outlined)
                }
            }
            ivReviewDetailHeart.setOnClickListener {
                infoReviewDetailViewModel.clickHeart()
            }

            ivInfoReviewDetailDot.setOnClickListener {
                showInfoReviewDeleteBottomSheet()
            }

            // 리뷰 댓글
            infoReviewCommentViewModel = ViewModelProvider(this@InfoReviewDetailFragment).get(InfoReviewCommentViewModel::class.java)

            rvReviewComment.apply {
                val layoutmanager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                layoutManager = layoutmanager
                adapter = infoReviewCommentRVAdapter

                val dividerItemDecoration = DividerItemDecoration(context, layoutmanager.orientation)
                addItemDecoration(dividerItemDecoration)
            }
            infoReviewCommentViewModel.getInfoReviewComment()
            infoReviewCommentViewModel.inforeviewcommentList.observe(viewLifecycleOwner, Observer { response ->
                infoReviewCommentRVAdapter.submitList(response)
                /*if (response.reply.isNullOrEmpty()) {
                    rvReviewComment.cl_info_review_comment_reply.visibility = View.GONE
                } else {
                    rvReviewComment.cl_info_review_comment_reply.visibility = View.VISIBLE
                }

                rvReviewComment.visibility = View.VISIBLE // RecyclerView 자체는 항상 보이게
                infoReviewCommentRVAdapter.submitList(response)*/

            })

            infoReviewCommentRVAdapter.setInfoReviewCommentItemClickListener(object : InfoReviewCommentRVAdapter.InfoReviewCommentItemClickListener{
                // 답글 화살표 클릭 시
                override fun onArrowClick(position: Int) {
                    val selectedArrow = infoReviewCommentRVAdapter.currentList[position]
                    val parentNavController = requireParentFragment().findNavController()
                    val action = InfoReviewDetailFragmentDirections.actionInfoReviewDetailFragmentToInfoReviewDetailReplyFragment(selectedArrow)
                    parentNavController.navigate(action)
                }
                // 댓글 점 클릭 시
                override fun onDotClick(position: Int) {
                    val item = infoReviewCommentRVAdapter.currentList.getOrNull(position)
                    if (item == null) {
                        return
                    }
                    InfoReviewCommentDeleteBottomSheet(
                        commentItemId = item.itemId,
                        onDeleteConfirmed = { deleteItemId ->
                            infoReviewCommentViewModel.deleteComment(deleteItemId)
                        }
                    ).show(parentFragmentManager, "delete comment")
                }
            })

            handleCommentUploadArea()
        }
    }

    private fun showInfoReviewDeleteBottomSheet() {
        InfoReviewDeleteBottomSheet().show(parentFragmentManager, "delete review")
    }

    // 댓글 게시 입력창 위치 조정
    private fun handleCommentUploadArea() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            binding.clInfoReviewUploadComment.translationY = if (isKeyboardVisible) {
                -keypadHeight.toFloat()
            } else {
                0f
            }
        }
    }
}
