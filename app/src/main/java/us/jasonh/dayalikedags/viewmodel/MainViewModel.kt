package us.jasonh.dayalikedags.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import us.jasonh.dayalikedags.UiState
import us.jasonh.dayalikedags.api.CatImageApi
import us.jasonh.dayalikedags.api.DogImageApi
import us.jasonh.dayalikedags.api.SunriseSunsetApi

class MainViewModel(
  private val sunriseSunsetApi: SunriseSunsetApi,
  private val dogImageApi: DogImageApi,
  private val catImageApi: CatImageApi,
) : BaseViewModel<UiState>() {

  fun performUpdate(location: Location) {
    uiState.value = UiState.Loading
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val sunriseSunset = sunriseSunsetApi.fetchSunriseSunset(location)
        Log.i("dags", "sunriseSunset: $sunriseSunset")
        // todo: add day/night conditional
        val imageUrl = dogImageApi.fetchRandomDogUrl()
        uiState.postValue(UiState.Success(imageUrl))

      } catch (exception: Exception) {
        Log.e("dags", "Network request failed. $exception")
        uiState.postValue(UiState.Error("Network request failed. $exception"))
      }
    }
  }

}
