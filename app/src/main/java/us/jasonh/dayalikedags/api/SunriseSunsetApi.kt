package us.jasonh.dayalikedags.api

import android.location.Location
import us.jasonh.dayalikedags.model.SunriseSunset
import us.jasonh.dayalikedags.model.SunriseSunsetResponse
import java.io.IOException
import javax.inject.Inject

class SunriseSunsetApi @Inject constructor() : Api() {

  override val apiHost: String = "https://api.sunrise-sunset.org"

  @Throws(IOException::class)
  fun fetchSunriseSunset(location: Location): SunriseSunset {
    val request = Request<SunriseSunsetResponse>("/json", HttpMethod.GET)
        .addQueryParam("lat", location.latitude.toString())
        .addQueryParam("lng", location.longitude.toString())
        .addQueryParam("formatted", "0")
    val sunriseSunsetResponse = fetchResponse(request)

    return sunriseSunsetResponse.sunriseSunset
  }

}
