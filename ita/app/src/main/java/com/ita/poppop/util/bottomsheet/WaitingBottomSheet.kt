package com.ita.poppop.util.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ita.poppop.data.remote.repository.review.ReviewRepository
import com.ita.poppop.data.remote.repository.review.ReviewRepositoryImpl
import com.ita.poppop.data.remote.repository.story.StoryRepository
import com.ita.poppop.data.remote.repository.story.StoryRepositoryImpl
import com.ita.poppop.databinding.FragmentWaitingBottomsheetBinding
import com.ita.poppop.util.dialog.WaitingDialog
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class WaitingBottomSheet(private val popupId: Int,private val storyId: Int) : BottomSheetDialogFragment() {

    private var _binding: FragmentWaitingBottomsheetBinding? = null
    private val binding get() = _binding!!
    private val repository: StoryRepository = StoryRepositoryImpl(RetrofitClient.storyApi)
    val mainAViewModel: MainAViewModel by activityViewModels()

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
                    lifecycleScope.launch {
                        try {
                            val result = withContext(Dispatchers.IO) {
                                repository.postDeleteStory(
                                    mainAViewModel.tokenPair.value.first.toString(),
                                    popupId,
                                    storyId
                                )
                            }

                            if (result.isSuccessful) {
                                dismiss()
                            }
                        } catch (e: HttpException) {
                            // HTTP 에러 상세 정보
                            Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                            Toast.makeText(context,"삭제 실패",Toast.LENGTH_SHORT).show()
                            dismiss()
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Exception: ${e.message}", e)
                            Toast.makeText(context,"삭제 실패",Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
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
