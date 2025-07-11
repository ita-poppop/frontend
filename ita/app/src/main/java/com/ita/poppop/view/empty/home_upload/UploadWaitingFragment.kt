package com.ita.poppop.view.empty.home_upload

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.core.os.BundleCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.story.StoryRepository
import com.ita.poppop.data.remote.repository.story.StoryRepositoryImpl
import com.ita.poppop.databinding.FragmentUploadWaitingBinding
import com.ita.poppop.model.empty.search.SearchMode
import com.ita.poppop.util.bottomsheet.UploadBottomSheet
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.empty.upload.UploadViewModel
import com.ita.poppop.viewmodel.empty.upload.UploadWaitingViewModel
import com.ita.poppop.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.math.absoluteValue


class UploadWaitingFragment : BaseFragment<FragmentUploadWaitingBinding>(R.layout.fragment_upload_waiting) {
    private lateinit var uploadWaitingViewModel: UploadWaitingViewModel
    val mainAViewModel: MainAViewModel by activityViewModels()
    private lateinit var mainViewModel: MainViewModel
    private val repository: StoryRepository = StoryRepositoryImpl(RetrofitClient.storyApi)
    private var uri : Uri? = null

    override fun initView() {
        setViewModel()
        setupWindowInsets()
        setupToolbar()
        setFragmentResult()
        setClickListener()

        binding.sbWaiting.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvWaitingCount.text = "${progress}명"
                binding.tvWaitingHint.text = when (progress) {
                    in 0..5 -> "매우 적어요! 기다리지 않고 바로 입장 가능해요"
                    in 6..15 -> "보통이에요. 살짝 기다릴 수 있어요"
                    in 16..30 -> "조금 많아요. 대기 줄이 있어요"
                    else -> "매우 많아요. 현재 웨이팅이 많아요, 참고해 주세요"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts dragging the thumb
                // You can add visual feedback here, e.g., change thumb color
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops dragging the thumb
                // You can finalize the action here
                uploadWaitingViewModel.setWaiting(seekBar?.progress)
            }
        })

    }
    private fun setViewModel() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        uploadWaitingViewModel = ViewModelProvider(this)[UploadWaitingViewModel::class.java]

        binding.uploadWaitingViewModel = uploadWaitingViewModel

        uploadWaitingViewModel.isAllValid.observe(viewLifecycleOwner) { valid ->
            Log.d("checkViewModel","waitingImage : ${uploadWaitingViewModel.waitingImage.value}")
            Log.d("checkViewModel","popupItem : ${uploadWaitingViewModel.popupItem.value}")
            Log.d("checkViewModel","waitingCount : ${uploadWaitingViewModel.waitingCount.value}")
            binding.btUploadWaiting.isEnabled = valid
        }
        // UploadItem 리스트를 관찰 (헤더가 포함된 리스트)
        uploadWaitingViewModel.waitingImage.observe(viewLifecycleOwner) { item ->
            setUploadUI(item)
        }
    }

    private fun setUploadUI(item: ImageItem?){

        if(item != null){
            binding.mcvImage.elevation = 2f
            Glide.with(binding.root)
                .load(item.uri)
                .centerCrop()
                .into(binding.ivWaiting)
        }else{
            binding.mcvImage.elevation = 0f
        }

    }

    private fun setClickListener() {
        binding.mcvWImgEdit.setOnClickListener {
            showUploadBottomSheet()
        }
        mainViewModel.selectItem.observe(viewLifecycleOwner) { seleteItem ->
            uploadWaitingViewModel.setPopupItem(seleteItem)
        }
        uploadWaitingViewModel.popupItem.observe(viewLifecycleOwner) { seleteItem ->
            binding.tvUploadLocation.text = seleteItem?.title ?: "팝업스토어 / 전시를 검색하세요"
        }
        binding.mcvWImgDelete.setOnClickListener {
            uploadWaitingViewModel.removeWaitingImage()
        }
        binding.rvUploadReview.setOnClickListener {
            showUploadBottomSheet()
        }
        binding.mcvSearchArea.setOnClickListener {
            navigateTo(UploadWaitingFragmentDirections.actionUploadWaitingFragmentToSearchFragment(
                SearchMode.RETURN_TO_UPLOAD))
        }
        binding.btUploadWaiting.setOnClickListener{

            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        Log.d("checkViewModelsid","uploadWaitingViewModel : ${uploadWaitingViewModel.createMultipartFromWaitingImage(requireContext())}")

                        repository.postUploadStory(
                            mainAViewModel.tokenPair.value.first.toString(),
                            mainViewModel.selectItem.value!!.id,
                            uploadWaitingViewModel.createMultipartFromWaitingImage(requireContext())!!,
                            0,
                            uploadWaitingViewModel.waitingCount.value!!.toInt()
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

    private fun setFragmentResult() {
        parentFragmentManager.setFragmentResultListener("upload_result", this) { _, bundle ->
            val uriList = BundleCompat.getParcelableArrayList(bundle, "images", Uri::class.java)
            uriList?.let { uris ->
                val imageItem = ImageItem(
                    id = uri.toString().hashCode().toLong().absoluteValue,
                    uri = uriList[0]
                )
                uploadWaitingViewModel.setWaitingImage(imageItem)
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
        UploadBottomSheet(1).show(parentFragmentManager, "upload_sheet")
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