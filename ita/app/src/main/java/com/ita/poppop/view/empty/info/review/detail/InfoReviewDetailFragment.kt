package com.ita.poppop.view.empty.info.review.detail

import android.graphics.Rect
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popup.CommentRepositoryImpl
import com.ita.poppop.data.remote.repository.popup.ReviewRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoReviewDetailBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentDeleteBottomSheet
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVAdapter
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentViewModel
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVAdapter
import com.ita.poppop.viewmodel.MainAViewModel

class InfoReviewDetailFragment : BaseFragment<FragmentInfoReviewDetailBinding>(R.layout.fragment_info_review_detail){

    private val infoReviewDetailArgs: InfoReviewDetailFragmentArgs by navArgs()
    val mainViewModel: MainAViewModel by activityViewModels()
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
            val reviewRepository = ReviewRepositoryImpl(RetrofitClient.reviewApi)
            val reviewFactory = ViewModelFactory { InfoReviewDetailViewModel(reviewRepository) }
            infoReviewDetailViewModel = ViewModelProvider(this@InfoReviewDetailFragment, reviewFactory)[InfoReviewDetailViewModel::class.java]
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
            reviewHeartClicked()

            ivReviewDetailHeart.setOnClickListener {
                infoReviewDetailViewModel.clickHeart()
            }

            ivInfoReviewDetailDot.setOnClickListener {
                showInfoReviewDeleteBottomSheet()
            }

            // 리뷰 댓글
            val commentRepository = CommentRepositoryImpl(RetrofitClient.commentApi)
            val commentFactory = ViewModelFactory { InfoReviewCommentViewModel(mainViewModel.tokenPair.value.first.toString(),commentRepository) }
            infoReviewCommentViewModel = ViewModelProvider(this@InfoReviewDetailFragment, commentFactory)[InfoReviewCommentViewModel::class.java]
            infoReviewCommentViewModel.getInfoReviewCommentList(infoReviewDetailArgs.review.itemId)
            infoReviewCommentViewModel.inforeviewcommentList.observe(viewLifecycleOwner) { commentList ->
                infoReviewCommentRVAdapter.submitList(commentList)
            }
            rvReviewComment.apply {
                val layoutmanager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                layoutManager = layoutmanager
                adapter = infoReviewCommentRVAdapter

                val dividerItemDecoration = DividerItemDecoration(context, layoutmanager.orientation)
                addItemDecoration(dividerItemDecoration)
            }

            tvUploadComment.setOnClickListener {
                val content = editUploadComment.text.toString().trim()
                if (content.isNotEmpty()) {
                    infoReviewCommentViewModel.postComment(infoReviewDetailArgs.review.itemId, content)
                    infoReviewCommentViewModel.getInfoReviewCommentList(infoReviewDetailArgs.review.itemId)

                    editUploadComment.text?.clear()

                    val keyboard = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    keyboard.hideSoftInputFromWindow(editUploadComment.windowToken, 0)
                    editUploadComment.clearFocus()
                }
            }

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

    private fun reviewHeartClicked() {
        binding.apply {
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
        }
    }

    // 댓글 게시 입력창 위치 조정
    private fun handleCommentUploadArea() {
        binding.apply {
            root.viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                root.getWindowVisibleDisplayFrame(rect)
                val screenHeight = root.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                val isKeyboardVisible = keypadHeight > screenHeight * 0.15

                clInfoReviewUploadComment.translationY = if (isKeyboardVisible) {
                    -keypadHeight.toFloat()
                } else {
                    0f
                }

                rvReviewComment.setPadding(
                    rvReviewComment.paddingLeft,
                    rvReviewComment.paddingTop,
                    rvReviewComment.paddingRight,
                    if (isKeyboardVisible) keypadHeight else 0
                )

                if (isKeyboardVisible) {
                    rvReviewComment.post {
                        rvReviewComment.scrollToPosition(infoReviewCommentRVAdapter.itemCount - 1)
                    }
                }
            }
        }
    }
}
