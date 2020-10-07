package us.jasonh.dayalikedags.api

import us.jasonh.dayalikedags.model.DogImageResponse
import javax.inject.Inject

class DogImageApi @Inject constructor() : Api() {

  override val apiHost: String = "https://random.dog"

  fun fetchRandomDogUrl(): String {
    val request = Request<DogImageResponse>("/woof.json", HttpMethod.GET)
    val dogImageResponse = fetchResponse(request)

    return dogImageResponse.url
  }

}
