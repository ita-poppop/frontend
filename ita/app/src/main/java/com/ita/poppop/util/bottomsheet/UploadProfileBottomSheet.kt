package com.ita.poppop.util.bottomsheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ita.poppop.R
import com.ita.poppop.databinding.FragmentUploadProfileBottomSheetBinding

class UploadProfileBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentUploadProfileBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val imageUris = mutableListOf<Uri>()
        result.data?.data?.let { uri ->
            imageUris.add(uri)
        }
        // 결과 전달
        parentFragmentManager.setFragmentResult(
            "upload_result",
            Bundle().apply {
                putParcelableArrayList("images", ArrayList(imageUris))
            }
        )

        dismiss() // 바텀시트 닫기
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUploadProfileBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.clDefaultArea.setOnClickListener {
            val imageUris = mutableListOf<Uri>()
            imageUris.add(Uri.parse("android.resource://com.ita.poppop/${R.drawable._profile_load_icon}"))
            parentFragmentManager.setFragmentResult(
                "upload_result",
                Bundle().apply {
                    putParcelableArrayList("images",ArrayList(imageUris))
                }
            )
            dismiss() // 바텀시트 닫기
        }

        binding.clGalleryArea.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
