package com.ita.poppop.view.main.favorites

import android.app.ProgressDialog.show
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popup.BookmarkRepositoryImpl
import com.ita.poppop.databinding.FragmentFavoritesBinding
import com.ita.poppop.databinding.ToastMessageBinding
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.hide
import com.ita.poppop.view.main.home.InfoFragment
import com.ita.poppop.view.main.show

class FavoritesFragment: BaseFragment<FragmentFavoritesBinding>(R.layout.fragment_favorites) {
    private lateinit var favoritesViewModel: FavoritesViewModel

    private val favoritesRVAdapter by lazy {
        FavoritesRVAdapter()
    }

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun initView() {
        binding.apply {
            //favoritesViewModel = ViewModelProvider(this@FavoritesFragment).get(FavoritesViewModel::class.java)

            val favoritesRepository = BookmarkRepositoryImpl(RetrofitClient.bookmarkApi)
            val favoritesFactory = ViewModelFactory { FavoritesViewModel(favoritesRepository) }
            favoritesViewModel = ViewModelProvider(this@FavoritesFragment, favoritesFactory)[FavoritesViewModel::class.java]
            linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            rvFavorites.apply {
                layoutManager = linearLayoutManager
                adapter = favoritesRVAdapter

                //val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
                //addItemDecoration(dividerItemDecoration)

                val itemTouchHelper = ItemTouchHelper(SwipeHelper())
                itemTouchHelper.attachToRecyclerView(this)
            }

            favoritesViewModel.getFavorites()
            favoritesViewModel.favoritesList.observe(viewLifecycleOwner, Observer { favoritesList ->
                favoritesRVAdapter.submitList(favoritesList)

                emptyStateLayout.root.run { if(favoritesList.isNullOrEmpty()) show() else hide()}
            })

            favoritesRVAdapter.setFavoritesItemClickListener(object : FavoritesRVAdapter.FavoritesItemClickListener{
                override fun onItemClick(position: Int) {
                    //val popupId = 1

                    val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                    val action = MainFragmentDirections.actionMainFragmentToNaviInfo()
                    parentNavController.navigate(action)
                }

                override fun onDeleteClick(position: Int) {
                    favoritesViewModel.deleteFavorites(position)

                    // toast message 띄우기
                    val removedItem = favoritesRVAdapter.currentList.getOrNull(position)?.title
                    val toastBinding = ToastMessageBinding.inflate(layoutInflater)
                    toastBinding.tvToastMessage.text = "즐겨찾기에서 삭제되었습니다."

                    Toast(requireContext()).apply {
                        duration = Toast.LENGTH_LONG
                        setGravity(Gravity.NO_GRAVITY, 0, 600)
                        view = toastBinding.root
                        show()
                    }
                }
            })
        }
    }

}