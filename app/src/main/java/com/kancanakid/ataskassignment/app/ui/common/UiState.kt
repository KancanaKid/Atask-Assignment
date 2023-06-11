package com.kancanakid.ataskassignment.app.ui.common

/**
 * @author basyi
 * Created 6/6/2023 at 1:03 PM
 */
sealed interface UiState {
    object Loading : UiState
    data class Error(val throwable: Throwable):UiState
    data class Success(val data:Any):UiState
}