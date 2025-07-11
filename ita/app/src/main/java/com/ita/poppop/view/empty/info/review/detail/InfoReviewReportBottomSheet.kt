package com.ita.poppop.view.empty.info.review.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ita.poppop.databinding.FragmentInfoReviewReportBottomSheetBinding

class InfoReviewReportBottomSheet(
    private val reviewItemId: Int,
    private val onReportConfirmed: (position: Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentInfoReviewReportBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoReviewReportBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.clReviewReport.setOnClickListener {
            showReviewReportDialog()
        }
    }

    private fun showReviewReportDialog() {
        InfoReviewReportDialog(requireContext()).apply {
            setItemClickListener(object : InfoReviewReportDialog.ItemClickListener {
                override fun onClick(message: String) {
                    onReportConfirmed(reviewItemId)  // 콜백 호출
                    dismiss()
                    this@InfoReviewReportBottomSheet.dismiss()
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