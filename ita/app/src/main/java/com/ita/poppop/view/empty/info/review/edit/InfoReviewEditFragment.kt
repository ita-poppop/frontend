package com.ita.poppop.view.empty.info.review.edit

import android.net.Uri
import android.util.Log
import androidx.core.os.BundleCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentInfoReviewEditBinding
import com.ita.poppop.util.bottomsheet.UploadBottomSheet
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import com.ita.poppop.view.empty.home_upload.sub.UploadImageAdapter
import com.ita.poppop.view.empty.home_upload.sub.UploadImageItemDecoration
import com.ita.poppop.view.empty.info.review.detail.InfoReviewDetailFragmentArgs
import com.ita.poppop.viewmodel.empty.upload.UploadViewModel
import kotlin.math.absoluteValue

class InfoReviewEditFragment: BaseFragment<FragmentInfoReviewEditBinding>(R.layout.fragment_info_review_edit) {

    private lateinit var editReviewImageAdapter: UploadImageAdapter
    private lateinit var editReviewViewModel: UploadViewModel

    override fun initView() {
        setupWindowInsets()
        setupToolbar()
        setupUploadRecycler()
        setFragmentResult()
        setViewModel()
    }


    private fun setViewModel() {
        editReviewViewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        // 바인딩에 ViewModel 연결
        binding.editReviewViewModel = editReviewViewModel

        // EditItem 리스트를 관찰 (헤더가 포함된 리스트)
        editReviewViewModel.uploadList.observe(viewLifecycleOwner) { uploadItemList ->
            editReviewImageAdapter.submitList(uploadItemList)
        }

        editReviewViewModel.isAllValid.observe(viewLifecycleOwner) { valid ->
            Log.d("checkViewModel","imageList : ${editReviewViewModel.imageList.value}")
            Log.d("checkViewModel","popupItem : ${editReviewViewModel.popupItem.value}")
            Log.d("checkViewModel","reviewContent : ${editReviewViewModel.reviewContent.value}")
            binding.btEdit.isEnabled = valid

        }
    }


    private fun setFragmentResult() {
        parentFragmentManager.setFragmentResultListener("upload_result", this) { _, bundle ->
            val uriList = BundleCompat.getParcelableArrayList(bundle, "images", Uri::class.java)
            uriList?.let { uris ->
                addImages(uris)
            }
        }
    }

    private fun setupUploadRecycler() = with(binding.rvEdit) {
        editReviewImageAdapter = UploadImageAdapter(
            onAddClick = {
                android.util.Log.d("checkList", "Add button clicked")
                showUploadBottomSheet()
            },
            onDeleteClick = { id ->
                android.util.Log.d("checkList", "Delete clicked for id: $id")
                deleteImage(id)
            }
        )
        adapter = editReviewImageAdapter
        layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        addItemDecoration(UploadImageItemDecoration())
    }

    private fun addImages(uris: List<Uri>) {
        uris.forEach { uri ->
            val imageItem = ImageItem(
                id = uri.toString().hashCode().toLong().absoluteValue,
                uri = uri
            )
            editReviewViewModel.addItem(imageItem)
            Log.d("checkList", "Adding image with id: ${imageItem.id}")
        }
    }

    private fun deleteImage(id: Long) {
        Log.d("checkList", "Deleting image with id: $id")
        editReviewViewModel.removeItem(id)
    }

    private fun setupToolbar() {
        binding.mtEdit.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener {
                InfoReviewEditDialog(requireContext()).apply {
                setItemClickListener(object : InfoReviewEditDialog.ItemClickListener {
                    override fun onClick(message: String) {
                        dismiss()
                    }

                    override fun onCancel(message: String) {
                        dismiss()
                        parentFragmentManager.popBackStack()
                    }
                })
                show()
            }}
        }
    }

    private fun navigateTo(action: NavDirections) {
        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
        navController.navigate(action)
    }

    private fun showUploadBottomSheet() {
        if(editReviewViewModel.getRemainingSlots() <= 0){
            view?.let { Snackbar.make(it, "이미지는 최대 5장까지 첨부할 수 있습니다.", Snackbar.LENGTH_SHORT).show() }
        }else{
            UploadBottomSheet(editReviewViewModel.getRemainingSlots()).show(parentFragmentManager, "Edit_sheet")
        }
    }

}