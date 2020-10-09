package us.jasonh.dayalikedags.model

import com.google.gson.annotations.SerializedName

data class SunriseSunsetResponse(
    @SerializedName("results") val sunriseSunset: SunriseSunset,
    val status: String
)
