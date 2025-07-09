package com.ita.poppop.view.empty.info.review.detail

import android.graphics.Rect
import androidx.activity.OnBackPressedCallback
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
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentReportBottomSheet
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
        setupBackPressedCallback()

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
            val popupId = infoReviewDetailArgs.popupId
            // 리뷰 상세 요청
            infoReviewDetailViewModel.getInfoReviewDetail(popupId,infoReviewDetailArgs.review.itemId)

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
                val accessToken = mainViewModel.tokenPair.value.first.toString()
                val reviewId = infoReviewDetailArgs.review.itemId
                infoReviewDetailViewModel.postReviewLikes(accessToken, reviewId)
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
                val currentUserId = getUserIdFromToken()
                val updatedList = commentList.map { comment ->
                    comment.apply { isMine = (writerId == currentUserId) }
                }
                infoReviewCommentRVAdapter.submitList(updatedList)
                //infoReviewCommentRVAdapter.submitList(commentList)
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
                    infoReviewCommentViewModel.postComment(infoReviewDetailArgs.review.itemId, content){
                        infoReviewDetailViewModel.getInfoReviewDetail(popupId,infoReviewDetailArgs.review.itemId)
                    }
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
                    val reviewId = infoReviewDetailArgs.review.itemId

                    val parentNavController = requireParentFragment().findNavController()
                    val action = InfoReviewDetailFragmentDirections.actionInfoReviewDetailFragmentToInfoReviewDetailReplyFragment(
                        comment = selectedArrow,
                        review = reviewId
                    )
                    parentNavController.navigate(action)
                }
                // 댓글 점 클릭 시
                override fun onDotClick(position: Int) {
                    val item = infoReviewCommentRVAdapter.currentList.getOrNull(position) ?: return

                    if (item.isMine == false) {
                        InfoReviewCommentDeleteBottomSheet(
                            commentItemId = item.itemId,
                            onDeleteConfirmed = { deleteItemId ->
                                infoReviewCommentViewModel.deleteComment(deleteItemId) {
                                    infoReviewDetailViewModel.getInfoReviewDetail(
                                        popupId,
                                        infoReviewDetailArgs.review.itemId
                                    )
                                }
                            }
                        ).show(parentFragmentManager, "delete comment")
                    } else {
                        InfoReviewCommentReportBottomSheet(
                            commentItemId = item.itemId,
                            onReportConfirmed = { reportedId ->
                                // 신고 후 처리 (토스트 등)
                            }
                        ).show(parentFragmentManager, "report comment")
                    }
                }
            })

            handleCommentUploadArea()
        }
    }

    private fun getUserIdFromToken(): String? {
        val token = mainViewModel.tokenPair.value.first ?: return null
        val parts = token.split(".")
        if (parts.size < 2) return null
        return try {
            val payloadJson = String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT))
            //val payloadJson = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
            val jsonObj = org.json.JSONObject(payloadJson)
            jsonObj.getString("sub")  // 토큰 payload에 userId가 "sub"에 있다고 가정
        } catch (e: Exception) {
            null
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

    private fun setupBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("reviewTab", 1)
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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
