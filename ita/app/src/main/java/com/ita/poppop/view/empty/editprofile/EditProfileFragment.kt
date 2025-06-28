package com.ita.poppop.view.empty.editprofile

import android.net.Uri
import android.util.Log
import androidx.core.os.BundleCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentEditProfileBinding
import com.ita.poppop.util.bottomsheet.UploadProfileBottomSheet
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import com.ita.poppop.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.math.absoluteValue

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {
    private lateinit var mainViewModel: MainViewModel
    override fun initView() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainViewModel.profileImage.observe(viewLifecycleOwner) { profileImage ->
            Glide.with(binding.root)
                .load(profileImage?.uri)
                .centerCrop()
                .into(binding.ivProfile)
        }
        setupWindowInsets()
        setupToolbar()
        setFragmentResult()
        setupProfileEditButton()
    }

    private fun setupToolbar() {
        binding.mtEditProfile.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
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
            val imageItem = ImageItem(
                id = uri.toString().hashCode().toLong().absoluteValue,
                uri = uri
            )
            mainViewModel.setProfileImage(imageItem)
            Log.d("checkList", "Adding image with id: ${imageItem.id}")
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
