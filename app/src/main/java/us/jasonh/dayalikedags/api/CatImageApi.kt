package us.jasonh.dayalikedags.api

import us.jasonh.dayalikedags.model.CatImageResponse
import java.io.IOException
import javax.inject.Inject

class CatImageApi @Inject constructor() : Api() {

  override val apiHost: String = "https://aws.random.cat"

  @Throws(IOException::class)
  fun fetchRandomCatUrl(): String {
    val request = Request<CatImageResponse>("/meow", HttpMethod.GET)
    val catImageResponse = fetchResponse(request)

    return catImageResponse.url
  }

}
