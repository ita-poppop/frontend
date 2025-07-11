package com.ita.poppop.view.empty.info.review.edit

import android.net.Uri
import android.util.Log
import androidx.core.os.BundleCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popups.ReviewRepository
import com.ita.poppop.data.remote.repository.popups.ReviewRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoReviewEditBinding
import com.ita.poppop.util.bottomsheet.UploadBottomSheet
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import com.ita.poppop.view.empty.home_upload.sub.UploadImageAdapter
import com.ita.poppop.view.empty.home_upload.sub.UploadImageItemDecoration
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.empty.upload.UploadViewModel
import com.ita.poppop.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import kotlin.math.absoluteValue

class InfoReviewEditFragment: BaseFragment<FragmentInfoReviewEditBinding>(R.layout.fragment_info_review_edit) {

    private val args: InfoReviewEditFragmentArgs by navArgs()
    private var popupId: Int = -1
    private var reviewId: Int = -1

    private lateinit var editReviewImageAdapter: UploadImageAdapter
    private lateinit var editReviewViewModel: UploadViewModel
    private lateinit var mainViewModel: MainViewModel
    val mainAViewModel: MainAViewModel by activityViewModels()
    private val repository: ReviewRepository = ReviewRepositoryImpl(RetrofitClient.reviewApi)
    override fun initView() {
        popupId = args.popupId
        reviewId = args.reviewId

        setupWindowInsets()
        setupToolbar()
        setupUploadRecycler()
        setFragmentResult()
        setViewModel()
        setupExistingContent()
        setClickListener()

        args.popupItem?.let {
            editReviewViewModel.setPopupItem(it)
        }


    }


    private fun setViewModel() {
        editReviewViewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        // 바인딩에 ViewModel 연결
        binding.editReviewViewModel = editReviewViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // UploadItem 리스트를 관찰 (헤더가 포함된 리스트)
        editReviewViewModel.uploadList.observe(viewLifecycleOwner) { editReviewItemList ->
            Log.d("ImageList", "Current upload list size: ${editReviewItemList.size}")
            editReviewImageAdapter.submitList(editReviewItemList.toList())
        }
        editReviewViewModel.popupItem.observe(viewLifecycleOwner) { seleteItem ->
            Log.d("editReviewFragment", "popupItem changed: $seleteItem")
            //binding.tvUploadLocation.text = seleteItem?.title ?: ""
        }
        mainViewModel.selectItem.observe(viewLifecycleOwner) { seleteItem ->
            editReviewViewModel.setPopupItem(seleteItem)
        }
        editReviewViewModel.isAllValid.observe(viewLifecycleOwner) { valid ->
            Log.d("editReviewFragment", "isAllValid changed: $valid")
            binding.btnReviewEdit.isEnabled = valid

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

    private fun setupUploadRecycler() = with(binding.rvReviewEdit) {
        editReviewImageAdapter = UploadImageAdapter(
            onAddClick = {
                Log.d("checkList", "Add button clicked")
                showUploadBottomSheet()
            },
            onDeleteClick = { id ->
                Log.d("checkList", "Delete clicked for id: $id")
                deleteImage(id)
            }
        )
        adapter = editReviewImageAdapter
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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

    private fun setClickListener() {
        binding.btnReviewEdit.setOnClickListener{
            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        val contentBody = editReviewViewModel.reviewContent.value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())
                        repository.modifyReview(
                            mainAViewModel.tokenPair.value.first.toString(),
                            reviewId,
                            contentBody,
                            editReviewViewModel.createMultipartListFromUris(requireContext())
                        )
                    }

                    if (result.isSuccessful) {
                        Log.d("ModifyReviewAPI_SUCCESS","result : ${result.body()}")
                        handleBackNavigation()
                    }
                } catch (e: HttpException) {
                    // HTTP 에러 상세 정보
                    Log.e("ModifyReviewAPI_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                } catch (e: Exception) {
                    Log.e("ModifyReviewAPI_ERROR", "Exception: ${e.message}", e)
                }
            }


        }
    }

    private fun setupExistingContent() {
        editReviewViewModel.setReviewContent(args.reviewContent)

        /*val existingImages = args.reviewImages?.toList() ?: emptyList()
        Log.d("setupExistingContent", "기존 이미지 개수: ${existingImages.size}")

        existingImages.forEach { imageUrl ->
            val imageItem = ImageItem(
                id = imageUrl.hashCode().toLong(),
                uri = Uri.parse(imageUrl)
            )
            Log.d("setupExistingContent", "기존 이미지 추가 id: ${imageItem.id}")
            editReviewViewModel.addItem(imageItem)
        }*/
    }

    private fun setupToolbar() {
        binding.mtReviewEdit.apply {
            setNavigationIcon(R.drawable.icon_x_close_b)
            setNavigationOnClickListener { handleBackNavigation() }
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
            UploadBottomSheet(editReviewViewModel.getRemainingSlots()).show(parentFragmentManager, "upload_sheet")
        }
    }
}