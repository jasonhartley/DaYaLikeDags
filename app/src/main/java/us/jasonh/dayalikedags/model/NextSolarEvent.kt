package us.jasonh.dayalikedags.model

import org.joda.time.DateTime
import us.jasonh.dayalikedags.toUserLocalTime

data class NextSolarEvent(
  val event: SolarEvent,
  val time: DateTime
) {
  enum class SolarEvent(val eventName: String) {
    SUNRISE("Sunrise"), SUNSET("Sunset")
  }

  override fun toString(): String {
    return "Next solar event: ${event.eventName} @ ${time.toUserLocalTime()}"
  }
}
