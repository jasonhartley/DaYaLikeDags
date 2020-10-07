package us.jasonh.dayalikedags.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.DateTime
import java.io.IOException
import java.lang.reflect.Type

abstract class Api {

  abstract val apiHost: String
  val httpClient: OkHttpClient = OkHttpClient()

  // Register all type adapters here.
  val gson: Gson = GsonBuilder()
    .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
    .create()

  open fun getAuthorizationToken(): String? {
    return null
  }

  @Throws(IOException::class)
  inline fun <reified T> fetchResponse(request: Request<T>): T {
    val requestUrl = apiHost + request.getUrlResource()
    val requestBodyJson = request.getBodyAsJsonString(gson)
    Log.i("dags", "${javaClass.simpleName} request url: $requestUrl, request body: $requestBodyJson")

    val requestBody = if (request.httpMethod === HttpMethod.GET) {
      null // GET method requests require a null body
    } else {
      requestBodyJson.toRequestBody(request.mediaType)
    }

    val okHttpRequestBuilder = okhttp3.Request.Builder()
      .url(requestUrl)
      .method(request.httpMethod.toString(), requestBody)
    if (request.requiresAuthorization) {
      val sessionToken = getAuthorizationToken()
      if (sessionToken == null) {
        Log.e("dags", "Request requires authorization, but the session token was null.")
      } else {
        okHttpRequestBuilder.addHeader("Authorization", "Bearer $sessionToken")
      }
    }

    val response = httpClient.newCall(okHttpRequestBuilder.build()).execute()
    val responseBody = response.body ?: throw IOException("Response body was null.")
    val responseBodyJson = responseBody.string() // this can throw IOException
    responseBody.close()
    Log.i("dags", "${javaClass.simpleName} $response, response body: $responseBodyJson")

    if (!response.isSuccessful) {
      throw IOException("${response.code} ${response.message} $responseBodyJson")
    }

    val typeOfT: Type = object : TypeToken<T>(){}.type
    return when (T::class) {
      ArrayList::class ->
        gson.fromJson(responseBodyJson, typeOfT)
      else ->
        gson.fromJson(responseBodyJson, T::class.java)
    }
  }

}
