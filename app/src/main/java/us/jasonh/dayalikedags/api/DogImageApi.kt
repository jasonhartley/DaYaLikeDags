package us.jasonh.dayalikedags.api

import us.jasonh.dayalikedags.model.DogImageResponse
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet

class DogImageApi @Inject constructor() : Api() {

  override val apiHost: String = "https://random.dog"
  private val unsupportedFileTypes = HashSet<String>(listOf("mp4"))

  fun fetchRandomDogUrl(): String {
    val request = Request<DogImageResponse>("/woof.json", HttpMethod.GET)
    var dogImageResponse: DogImageResponse
    var fetchedFileExtension: String
    do {
      dogImageResponse = fetchResponse(request)
      fetchedFileExtension = File(dogImageResponse.url).extension.toLowerCase(Locale.ROOT)
    } while (unsupportedFileTypes.contains(fetchedFileExtension))

    return dogImageResponse.url
  }

}
