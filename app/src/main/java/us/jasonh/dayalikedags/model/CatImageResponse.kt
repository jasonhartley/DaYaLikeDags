package us.jasonh.dayalikedags.model

import com.google.gson.annotations.SerializedName

data class CatImageResponse(
  @SerializedName("file") val url: String
)
