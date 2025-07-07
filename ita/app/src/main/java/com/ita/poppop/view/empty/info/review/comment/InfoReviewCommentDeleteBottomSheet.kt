package com.ita.poppop.view.empty.info.review.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ita.poppop.databinding.FragmentInfoReviewCommentDeleteBottomSheetBinding

class InfoReviewCommentDeleteBottomSheet(
    private val commentItemId: Int,
    private val onDeleteConfirmed: (position: Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentInfoReviewCommentDeleteBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoReviewCommentDeleteBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.clCommentDelete.setOnClickListener {
            showCommentDeleteDialog()
        }
    }

    private fun showCommentDeleteDialog() {
        InfoReviewCommentDeleteDialog(requireContext()).apply {
            setItemClickListener(object : InfoReviewCommentDeleteDialog.ItemClickListener {
                override fun onClick(message: String) {
                    onDeleteConfirmed(commentItemId)  // 콜백 호출
                    dismiss()
                    this@InfoReviewCommentDeleteBottomSheet.dismiss()
                }
            })
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}