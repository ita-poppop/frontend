package com.ita.poppop.view.empty.home_upload

import android.net.Uri
import android.util.Log
import android.widget.SeekBar
import androidx.core.os.BundleCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.story.StoryRepository
import com.ita.poppop.data.remote.repository.story.StoryRepositoryImpl
import com.ita.poppop.databinding.FragmentUploadWaitingBinding
import com.ita.poppop.util.bottomsheet.UploadBottomSheet
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.empty.upload.UploadViewModel
import com.ita.poppop.viewmodel.empty.upload.UploadWaitingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.math.absoluteValue


class UploadWaitingFragment : BaseFragment<FragmentUploadWaitingBinding>(R.layout.fragment_upload_waiting) {
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var uploadWaitingViewModel: UploadWaitingViewModel
    val mainAViewModel: MainAViewModel by activityViewModels()
    private val repository: StoryRepository = StoryRepositoryImpl(RetrofitClient.storyApi)
    private var uri : Uri? = null

    override fun initView() {
        setupWindowInsets()
        setupToolbar()
        setFragmentResult()
        setClickListener()
        setViewModel()
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
        uploadViewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        uploadWaitingViewModel = ViewModelProvider(this)[UploadWaitingViewModel::class.java]

        binding.uploadWaitingViewModel = uploadWaitingViewModel

        uploadWaitingViewModel.setPopupItem("ddd")
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
        binding.mcvWImgDelete.setOnClickListener {
            uploadWaitingViewModel.removeWaitingImage()
        }
        binding.rvUploadReview.setOnClickListener {
            showUploadBottomSheet()
        }
        binding.btUploadWaiting.setOnClickListener{

            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        Log.d("checkViewModelsid","uploadWaitingViewModel : ${uploadWaitingViewModel.createMultipartFromWaitingImage(requireContext())}")

                        repository.postUploadStory(
                            mainAViewModel.tokenPair.value.first.toString(),
                            2310,
                            uploadWaitingViewModel.createMultipartFromWaitingImage(requireContext())!!,
                            0,
                            uploadWaitingViewModel.waitingCount.value!!.toInt()
                        )
                    }

                    if (result.isSuccessful) {
                        Log.d("checkUploadData","result : ${result.body()}")
                        handleBackNavigation()
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


    private fun showUploadBottomSheet() {
        UploadBottomSheet(1).show(parentFragmentManager, "upload_sheet")
    }
}