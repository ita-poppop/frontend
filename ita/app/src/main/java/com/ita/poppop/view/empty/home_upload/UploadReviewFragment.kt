package com.ita.poppop.view.empty.home_upload

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.os.BundleCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.review.ReviewRepository
import com.ita.poppop.data.remote.repository.review.ReviewRepositoryImpl
import com.ita.poppop.databinding.FragmentUploadReviewBinding
import com.ita.poppop.model.empty.search.SearchMode
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
import retrofit2.HttpException
import kotlin.math.absoluteValue

class UploadReviewFragment : BaseFragment<FragmentUploadReviewBinding>(R.layout.fragment_upload_review) {
    private lateinit var uploadImageAdapter: UploadImageAdapter
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var mainViewModel: MainViewModel
    val mainAViewModel: MainAViewModel by activityViewModels()
    private val repository: ReviewRepository = ReviewRepositoryImpl(RetrofitClient.reviewApi)
    override fun initView() {
        setViewModel()
        setupWindowInsets()
        setupToolbar()
        setupUploadRecycler()
        setFragmentResult()
        setClickListener()

    }


    private fun setViewModel() {
        uploadViewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        // 바인딩에 ViewModel 연결
        binding.uploadReviewViewModel = uploadViewModel

        // UploadItem 리스트를 관찰 (헤더가 포함된 리스트)
        uploadViewModel.uploadList.observe(viewLifecycleOwner) { uploadItemList ->
            uploadImageAdapter.submitList(uploadItemList)
        }
        uploadViewModel.popupItem.observe(viewLifecycleOwner) { selectedItem ->
            binding.tvUploadLocation.text = selectedItem?.title ?: "팝업스토어 / 전시를 검색하세요"
        }
        mainViewModel.selectItem.observe(viewLifecycleOwner) { selectItem ->
            uploadViewModel.setPopupItem(selectItem)
        }
        uploadViewModel.isAllValid.observe(viewLifecycleOwner) { valid ->
            binding.btUploadReview.isEnabled = valid

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

    private fun setupUploadRecycler() = with(binding.rvUploadReview) {
        uploadImageAdapter = UploadImageAdapter(
            onAddClick = {
                Log.d("checkList", "Add button clicked")
                showUploadBottomSheet()
            },
            onDeleteClick = { id ->
                Log.d("checkList", "Delete clicked for id: $id")
                deleteImage(id)
            }
        )
        adapter = uploadImageAdapter
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addItemDecoration(UploadImageItemDecoration())
    }

    private fun addImages(uris: List<Uri>) {
        uris.forEach { uri ->
            val imageItem = ImageItem(
                id = uri.toString().hashCode().toLong().absoluteValue,
                uri = uri
            )
            uploadViewModel.addItem(imageItem)
            Log.d("checkList", "Adding image with id: ${imageItem.id}")
        }
    }

    private fun deleteImage(id: Long) {
        Log.d("checkList", "Deleting image with id: $id")
        uploadViewModel.removeItem(id)
    }

    private fun setClickListener() {
        binding.mcvSearchArea.setOnClickListener {
            navigateTo(UploadReviewFragmentDirections.actionUploadReviewFragmentToSearchFragment(
                SearchMode.RETURN_TO_UPLOAD))
        }
        binding.btUploadReview.setOnClickListener{
            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {


                        repository.postReview(
                            mainAViewModel.tokenPair.value.first.toString(),
                            mainViewModel.selectItem.value!!.id,
                            uploadViewModel.reviewContent.value.toString(),
                            uploadViewModel.createMultipartListFromUris(requireContext())
                        )
                    }

                    if (result.isSuccessful) {
                        Log.d("checkUploadData","result : ${result.body()}")
                        handleBackNavigation()
                        mainViewModel.setSelectItem(null)
                    }
                } catch (e: HttpException) {
                    // HTTP 에러 상세 정보
                    Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Exception: ${e.message}", e)
                }
            }


        }
    }

    private fun setupToolbar() {
        binding.mtUploadReview.apply {
            setNavigationIcon(R.drawable.icon_x_close_b)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }

    private fun navigateTo(action: NavDirections) {
        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
        navController.navigate(action)
    }

    private fun showUploadBottomSheet() {
        if(uploadViewModel.getRemainingSlots() <= 0){
            view?.let { Snackbar.make(it, "이미지는 최대 5장까지 첨부할 수 있습니다.", Snackbar.LENGTH_SHORT).show() }
        }else{
            UploadBottomSheet(uploadViewModel.getRemainingSlots()).show(parentFragmentManager, "upload_sheet")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("UploadReviewFragment","onDestroy")
        mainViewModel.setSelectItem(null)
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("UploadReviewFragment","onDetach")
        mainViewModel.setSelectItem(null)
    }
}