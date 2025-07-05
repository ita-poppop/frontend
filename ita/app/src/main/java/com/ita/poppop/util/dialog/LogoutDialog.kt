package com.ita.poppop.util.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.ita.poppop.R
import com.ita.poppop.databinding.DialogLogoutBinding
import com.ita.poppop.util.LoginManager
import com.ita.poppop.view.MainActivity
import com.ita.poppop.viewmodel.MainAViewModel

class LogoutDialog(
    context: Context
    ) : Dialog(context) {
    private var itemClickListener: ItemClickListener? = null
    private lateinit var binding: DialogLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        setupDialog()
        bindView()
    }

    private fun initBinding() {
        binding = DialogLogoutBinding.inflate(LayoutInflater.from(context))
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
