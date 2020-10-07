package us.jasonh.dayalikedags.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class Request<T>(
    private val path: String,
    val httpMethod: HttpMethod
) {
  var requiresAuthorization: Boolean = false
  val mediaType: MediaType? = "application/json".toMediaTypeOrNull()
  private val pathParams: MutableList<Pair<String, String>> = ArrayList()
  private val queryParams: MutableList<Pair<String, String>> = ArrayList()
  private val bodyParams: MutableList<Pair<String, Any>> = ArrayList()
  private var payload: Any? = null

  fun enforceAuthorization(): Request<T> {
    requiresAuthorization = true
    return this
  }

  fun addPathParam(key: String, value: String): Request<T> {
    pathParams.add(Pair(key, value))
    return this
  }

  fun addQueryParam(key: String, value: String): Request<T> {
    queryParams.add(Pair(key, value))
    return this
  }

  fun addBodyParam(key: String, value: String): Request<T> {
    bodyParams.add(Pair(key, value))
    return this
  }

  fun addBodyParam(key: String, value: Number): Request<T> {
    bodyParams.add(Pair(key, value))
    return this
  }

  fun addBodyParam(key: String, value: Boolean): Request<T> {
    bodyParams.add(Pair(key, value))
    return this
  }

  fun setPayload(payload: Any?): Request<T> {
    this.payload = payload
    return this
  }

  fun getBodyAsJsonString(gson: Gson): String {
    val requestBodyJson = if (payload == null) JsonObject() else gson.toJsonTree(payload).asJsonObject
    for ((key, value) in bodyParams) {
      when (value) {
        is String -> requestBodyJson.addProperty(key, value)
        is Number -> requestBodyJson.addProperty(key, value)
        is Boolean -> requestBodyJson.addProperty(key, value)
        else -> Log.e("tag", "message", IllegalArgumentException("Request param value is an "
            + "unsupported type and will be ignored. key: $key, value: $value"))
      }
    }
    return requestBodyJson.toString()
  }

  fun getUrlResource(): String {
    // replace path params
    var populatedPath = path
    for ((key, value) in pathParams) {
      val replace = "{$key}"
      populatedPath = populatedPath.replace(replace, value)
    }

    // append query params
    val pathBuilder = StringBuilder(populatedPath)
    var isFirstQueryParam = true
    for ((key, value) in queryParams) {
      pathBuilder.append(if (isFirstQueryParam) "?" else "&")
          .append("$key=$value")
      isFirstQueryParam = false
    }

    return pathBuilder.toString()
  }

}
