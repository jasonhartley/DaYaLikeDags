package us.jasonh.dayalikedags.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import us.jasonh.dayalikedags.api.CatImageApi
import us.jasonh.dayalikedags.api.DogImageApi
import us.jasonh.dayalikedags.api.SunriseSunsetApi
import us.jasonh.dayalikedags.model.NextSolarEvent

class MainViewModel(
  private val sunriseSunsetApi: SunriseSunsetApi,
  private val dogImageApi: DogImageApi,
  private val catImageApi: CatImageApi,
) : BaseViewModel<UiState>() {

  fun performUpdate(location: Location) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val sunriseSunset = sunriseSunsetApi.fetchSunriseSunset(location)

        val nextSolarEvent = if (sunriseSunset.sunrise.isBeforeNow && sunriseSunset.sunset.isAfterNow) {
          NextSolarEvent(NextSolarEvent.SolarEvent.SUNSET, sunriseSunset.sunset)
        } else {
          NextSolarEvent(NextSolarEvent.SolarEvent.SUNRISE, sunriseSunset.sunrise)
        }

        val imageUrl = if (nextSolarEvent.event == NextSolarEvent.SolarEvent.SUNSET) {
          catImageApi.fetchRandomCatUrl()
        } else {
          dogImageApi.fetchRandomDogUrl()
        }

        uiState.postValue(UiState.Success(imageUrl, location, nextSolarEvent))

      } catch (exception: Exception) {
        Log.e("dags1", "Network request failed. $exception")
        uiState.postValue(UiState.Error("Network request failed. $exception"))
      }
    }
  }

}
