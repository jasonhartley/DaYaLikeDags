package us.jasonh.dayalikedags.viewmodel

import android.location.Location
import us.jasonh.dayalikedags.model.NextSolarEvent

sealed class UiState {

  data class Success(
    val imageUrl: String,
    val location: Location,
    val nextSolarEvent: NextSolarEvent) : UiState()

  data class Error(
    val message: String
  ) : UiState()

}
