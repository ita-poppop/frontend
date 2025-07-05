package com.ita.poppop.data.remote.repository.popups

import com.ita.poppop.data.remote.dto.popups.PopupDetailData

interface PopupDetailRepository {
    suspend fun getPopupDetail(popupId: Int): PopupDetailData?
}