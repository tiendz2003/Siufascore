package com.jerry.ronaldo.siufascore.presentation.setting

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.User

data class SettingUiState(
    val isLoading: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val errorMessage: String? = null,
    val userInfo:User ?= null
) :ViewState
sealed class SettingIntent:Intent{
    data class ToggleNotification(val isEnabled: Boolean) : SettingIntent()
    data class UpdateUserInfo(val user: User) : SettingIntent()
    data object LoadUserInfo : SettingIntent()
    data object Signout : SettingIntent()
}
sealed class SettingEvent:SingleEvent{
    data class ShowError(val message: String) : SettingEvent()
    data object SignoutSuccess : SettingEvent()
    data object UserInfoUpdated : SettingEvent()
    data object UserInfoLoaded : SettingEvent()
}