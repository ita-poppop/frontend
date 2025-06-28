package com.ita.poppop.util.bottomsheet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ita.poppop.databinding.FragmentUploadBottomSheetBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadBottomSheet(var maxImages : Int = 5) : BottomSheetDialogFragment() {

    private var _binding: FragmentUploadBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var photoFile: File? = null
    private var photoUri: Uri? = null

    private val requestGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val clipData = result.data?.clipData
            val imageUris = mutableListOf<Uri>()

            if (clipData != null) {
                // 여러 장 선택한 경우
                val totalSelected = clipData.itemCount
                val imagesToProcess = minOf(totalSelected, maxImages)

                for (i in 0 until imagesToProcess) {
                    val imageUri = clipData.getItemAt(i).uri
                    imageUris.add(imageUri)
                }

                // 선택 결과에 따른 메시지 표시
                when {
                    totalSelected > maxImages -> {
                        Toast.makeText(
                            requireContext(),
                            "최대 ${maxImages}장까지만 선택할 수 있습니다. 처음 ${maxImages}장이 선택되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    totalSelected > 1 -> {
                        Toast.makeText(
                            requireContext(),
                            "${totalSelected}장의 이미지가 선택되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // 한 장만 선택한 경우
                result.data?.data?.let { uri ->
                    imageUris.add(uri)
                }
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
    }

    private val requestCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == android.app.Activity.RESULT_OK) {
            parentFragmentManager.setFragmentResult(
                "upload_result",
                Bundle().apply {
                    putParcelableArrayList("images", arrayListOf(photoUri!!))
                }
            )

            dismiss() // 바텀시트 닫기
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.clOpenGalleryArea.setOnClickListener { openGallery() }
        binding.clTakeCameraArea.setOnClickListener { openCamera() }
    }

    private fun openGallery() {
        if (maxImages > 1){
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                // 일부 갤러리 앱에서 지원하는 최대 선택 개수 제한
                putExtra("android.intent.extra.MAX_NUM", maxImages)
            }
            requestGalleryLauncher.launch(intent)
        }else{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            requestGalleryLauncher.launch(intent)

        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canResolve = intent.resolveActivity(requireActivity().packageManager) != null

        if (canResolve) {
            try {
                val file = createImageFile()
                photoFile = file
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.ita.poppop.fileprovider",
                    file
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                requestCameraLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "카메라 파일 생성 실패", e)
            }
        } else {
            Log.e(TAG, "카메라 앱이 없습니다")
            // TODO: 사용자에게 카메라 앱 없음 알림
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "UploadBottomSheet"
    }
}