package com.ita.poppop.view.empty.info.review.detail.reply


import android.graphics.Rect
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popup.CommentRepositoryImpl
import com.ita.poppop.data.remote.repository.popup.ReviewRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoReviewDetailReplyBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentDeleteBottomSheet
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVAdapter
import com.ita.poppop.view.empty.info.review.detail.InfoReviewDeleteBottomSheet
import com.ita.poppop.view.empty.info.review.detail.InfoReviewDetailFragmentArgs
import com.ita.poppop.view.empty.info.review.detail.InfoReviewDetailFragmentDirections
import com.ita.poppop.view.empty.info.review.detail.InfoReviewDetailViewHolder
import com.ita.poppop.view.empty.info.review.detail.InfoReviewDetailViewModel

class InfoReviewDetailReplyFragment : BaseFragment<FragmentInfoReviewDetailReplyBinding>(R.layout.fragment_info_review_detail_reply){

    private val infoReviewDetailReplyArgs: InfoReviewDetailReplyFragmentArgs by navArgs()

    private lateinit var infoReviewDetailReplyViewModel: InfoReviewDetailReplyViewModel
    private lateinit var infoReviewCommentDetailViewHolder: InfoReviewCommentDetailViewHolder

    private val infoReviewDetailReplyRVAdapter by lazy {
        InfoReviewDetailReplyRVAdapter()
    }

    override fun initView() {
        setupWindowInsets()
        binding.apply {

            // 뒤로 가기
            ivReviewDetailReplyBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            // 댓글 삭제 신고 바텀 시트
            ivInfoReviewCommentDot.setOnClickListener {
                showInfoReviewCommentDeleteBottomSheet()
            }

            // 댓글 상세
            val repository = CommentRepositoryImpl(RetrofitClient.commentApi)
            val factory = ViewModelFactory { InfoReviewDetailReplyViewModel(repository) }
            infoReviewDetailReplyViewModel = ViewModelProvider(this@InfoReviewDetailReplyFragment, factory)[InfoReviewDetailReplyViewModel::class.java]
            infoReviewCommentDetailViewHolder = InfoReviewCommentDetailViewHolder(binding)

            // 댓글 상세 요청
            infoReviewDetailReplyViewModel.getInfoCommentDetail(infoReviewDetailReplyArgs.comment.itemId, infoReviewDetailReplyArgs.comment.itemId)
            infoReviewDetailReplyViewModel.infocommentdetail.observe(viewLifecycleOwner) { comment ->
                infoReviewCommentDetailViewHolder.bind(comment)
            }

            // 리뷰 대댓글
            rvReviewReply.apply {
                infoReviewDetailReplyViewModel.getInfoCommentDetail(infoReviewDetailReplyArgs.comment.itemId, infoReviewDetailReplyArgs.comment.itemId)

                val layoutmanager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                layoutManager = layoutmanager
                adapter = infoReviewDetailReplyRVAdapter

                infoReviewDetailReplyViewModel.inforeviewdetailreplyList.observe(viewLifecycleOwner) { replyList ->
                    infoReviewDetailReplyRVAdapter.submitList(replyList.toList())
                }
            }

            
            tvWriteReply.setOnClickListener {
                editUploadCommentReply.requestFocus()
                // 키보드 열기
                val keyboard = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                keyboard.showSoftInput(editUploadCommentReply, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }

            tvUploadCommentReply.setOnClickListener {
                val reply = editUploadCommentReply.text.toString().trim()
                if (reply.isNotEmpty()) {
                    infoReviewDetailReplyViewModel.addReply(reply)

                    editUploadCommentReply.text?.clear()

                    val keyboard = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    keyboard.hideSoftInputFromWindow(editUploadCommentReply.windowToken, 0)
                    editUploadCommentReply.clearFocus()
                }
            }

            infoReviewDetailReplyRVAdapter.setInfoReviewDetailReplyItemClickListener(object : InfoReviewDetailReplyRVAdapter.InfoReviewDetailReplyItemClickListener{
                // 답글 점 클릭 시
                override fun onDotClick(position: Int) {
                    val item = infoReviewDetailReplyRVAdapter.currentList.getOrNull(position)
                    if (item == null) {
                        return
                    }
                    InfoReviewCommentDeleteBottomSheet(
                        commentItemId = item.itemId,
                        onDeleteConfirmed = { deleteItemId ->
                            infoReviewDetailReplyViewModel.deleteReply(deleteItemId)
                        }
                    ).show(parentFragmentManager, "delete reply")
                }
            })
            handleReplyUploadArea()
        }
    }

    private fun showInfoReviewCommentDeleteBottomSheet() {
        //InfoReviewCommentDeleteBottomSheet().show(parentFragmentManager, "delete comment")
    }

    // 대댓글 게시 입력창 위치 조정
    private fun handleReplyUploadArea() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            binding.clInfoReviewUploadCommentReply.translationY = if (isKeyboardVisible) {
                -keypadHeight.toFloat()
            } else {
                0f
            }
        }
    }
}
