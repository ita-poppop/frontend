package com.ita.poppop.view.empty.editprofile

import android.net.Uri
import android.util.Log
import androidx.core.os.BundleCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.data.remote.repository.profile.ProfileRepository
import com.ita.poppop.data.remote.repository.profile.ProfileRepositoryImpl
import com.ita.poppop.databinding.FragmentEditProfileBinding
import com.ita.poppop.util.bottomsheet.UploadProfileBottomSheet
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.home_upload.UploadReviewFragmentDirections
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.main.MainViewModel
import com.ita.poppop.viewmodel.main.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.absoluteValue

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {
    private lateinit var mainViewModel: MainViewModel
    val mainAViewModel: MainAViewModel by activityViewModels()
    private val repository: ProfileRepository = ProfileRepositoryImpl(RetrofitClient.profileApi)
    override fun initView() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setData()
        setClickListener()
        setupWindowInsets()
        setupToolbar()
        setFragmentResult()
        setupProfileEditButton()
        setupEditText()

    }

    private fun setData(){
        mainViewModel.userData.observe(viewLifecycleOwner) { it ->
            Glide.with(binding.root)
                .load(it?.userProfileImage)
                .error(R.drawable._profile_load_icon)
                .centerCrop()
                .into(binding.ivProfile)
        }
        binding.etProfileName.hint = mainViewModel.userData.value?.userName?.replace("\"", "")
    }

    private fun setupToolbar() {
        binding.mtEditProfile.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }

    private fun setClickListener() {
        binding.mcvEditProfile.setOnClickListener {
            lifecycleScope.launch {

                try {
                    val result = withContext(Dispatchers.IO) {
                        repository.postProfile(
                            mainAViewModel.tokenPair.value.first.toString(),
                            mainViewModel.createMultipartFromProfileImage(requireContext()),
                            binding.etProfileName.text.toString()
                        )
                    }

                    if (result.isSuccessful) {
                        Log.d("checkUploadData","qwerasdf : ${mainViewModel.createMultipartFromProfileImage(requireContext())}")


                        mainViewModel.editUserProfile(
                            binding.etProfileName.text.toString(),
                            mainViewModel.userData.value!!.userProfileImage.toString()
                        )
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

    private fun setupEditText(){


    }


    private fun setupProfileEditButton() {
        binding.mcvProfileEdit.setOnClickListener {
            showProfileUploadBottomSheet()
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
    private fun addImages(uris: List<Uri>) {
        uris.forEach { uri ->
            mainViewModel.editUserProfileImage(uri.toString())
        }
    }
    private fun showProfileUploadBottomSheet() {
        UploadProfileBottomSheet().show(
            parentFragmentManager,
            PROFILE_SETTING_DIALOG_TAG
        )
    }

    companion object {
        private const val PROFILE_SETTING_DIALOG_TAG = "profile_setting_dialog"
    }



}
