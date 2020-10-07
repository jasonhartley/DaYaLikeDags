package us.jasonh.dayalikedags

sealed class UiState {
  object Loading : UiState()
  data class Success(val imageUrl: String) : UiState()
  data class Error(val message: String) : UiState()
}
