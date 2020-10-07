package us.jasonh.dayalikedags.model

import org.joda.time.DateTime

data class SunriseSunset(
  val sunrise: DateTime,
  val sunset: DateTime

  /** available but currently unused fields:
  @SerializedName("solar_noon") val solarNoon: DateTime? = null,
  @SerializedName("civil_twilight_begin") val civilTwilightBegin: DateTime? = null,
  @SerializedName("civil_twilight_end") val civilTwilightEnd: DateTime? = null,
  @SerializedName("nautical_twilight_begin") val nauticalTwilightBegin: DateTime? = null,
  @SerializedName("nautical_twilight_end") val NauticalTwilightEnd: DateTime? = null,
  @SerializedName("astronomical_twilight_begin") val astronomicalTwilightBegin: DateTime? = null,
  @SerializedName("astronomical_twilight_end") val astronomicalTwilightEnd: DateTime? = null
  */
)
