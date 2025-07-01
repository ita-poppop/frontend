package com.ita.poppop.view.empty.info.review.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.R

class InfoReviewCommentViewModel :ViewModel() {

    private val _inforeviewcommentList = MutableLiveData<MutableList<InfoReviewCommentRVItem>>()
    val inforeviewcommentList: LiveData<MutableList<InfoReviewCommentRVItem>> = _inforeviewcommentList

    // 리뷰 상세 화면에서 댓글 추가
    fun addComment(content: String) {
        val currentList = _inforeviewcommentList.value ?: mutableListOf()
        val newId = (currentList.maxOfOrNull { it.itemId } ?: 0) + 1
        val newComment = InfoReviewCommentRVItem(
            itemId = newId,
            username = "hello",
            profileImage = R.drawable._profile_load_icon,
            content = content,
            time = "방금 전",
            reply = 0
        )
        val updatedList = currentList.toMutableList()
        updatedList.add(newComment)
        _inforeviewcommentList.value = updatedList

    }
    
    // 리뷰 상세 화면에서 댓글 삭제
    fun deleteComment(commentItemId: Int) {
        val currentList = _inforeviewcommentList.value ?: return
        Log.d("DeleteComment", "Deleting id: $commentItemId")
        Log.d("DeleteComment", "Before delete: ${currentList.map { it.itemId }}")
        val updatedList = currentList.filterNot { it.itemId == commentItemId }.toMutableList()
        Log.d("DeleteComment", "After delete: ${updatedList.map { it.itemId }}")
        _inforeviewcommentList.value = updatedList
    }

    fun getInfoReviewComment(){
        val list = mutableListOf<InfoReviewCommentRVItem>()
        /*list.clear()*/
        list.add(
            InfoReviewCommentRVItem(
                1,
                R.drawable.main_btn_favorites_icon,
                "wild_zeal",
                "6일 전",
                "헉 ㅠㅠ 너무 가고 싶어요ㅠ",
                4
            )
        )
        list.add(
            InfoReviewCommentRVItem(
                2,
                R.drawable.main_btn_favorites_icon,
                "skyline_7",
                "6일 전",
                "전시가 애니 속 장면들을 잘 살려놔서 보는 내내 몰입감 장난 아니었어요.'거짓말과 아이', '빛과 그림자' 같은 테마도 은근 생각하게 만들더라구요.",
                null
            )
        )
        list.add(
            InfoReviewCommentRVItem(
                3,
                R.drawable.main_btn_favorites_icon,
                "wild_zeal",
                "6일 전",
                "전시가 애니 속 장면들을 잘 살려놔서 보는 내내 몰입감 장난 아니었어요.'거짓말과 아이', '빛과 그림자' 같은 테마도 은근 생각하게 만들더라구요.",
                3
            )
        )

        _inforeviewcommentList.value = list
    }
}