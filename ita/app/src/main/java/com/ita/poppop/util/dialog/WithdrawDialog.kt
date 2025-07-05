package com.ita.poppop.util.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.ita.poppop.databinding.DialogWaitingBinding
import com.ita.poppop.databinding.DialogWithdrawBinding

class WithdrawDialog(context: Context) : Dialog(context) {

    private var itemClickListener: ItemClickListener? = null
    private lateinit var binding: DialogWithdrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setupDialog()
        bindView()
    }

    private fun initBinding() {
        binding = DialogWithdrawBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    private fun setupDialog() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    private fun bindView() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            itemClickListener?.onClick()
            dismiss()
        }
    }

    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    interface ItemClickListener {
        fun onClick()
    }
}
