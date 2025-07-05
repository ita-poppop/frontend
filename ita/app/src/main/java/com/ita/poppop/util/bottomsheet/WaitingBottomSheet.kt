package com.ita.poppop.util.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ita.poppop.databinding.FragmentWaitingBottomsheetBinding
import com.ita.poppop.util.dialog.WaitingDialog

class WaitingBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentWaitingBottomsheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitingBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.clWaitingConfirm.setOnClickListener {
            showWaitingDialog()
        }
    }

    private fun showWaitingDialog() {
        WaitingDialog(requireContext()).apply {
            setItemClickListener(object : WaitingDialog.ItemClickListener {
                override fun onClick() {
                    // TODO: 전화번호 혹은 액션 처리
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
