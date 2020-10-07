package us.jasonh.dayalikedags.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type

/**
 * Borrowed from https://github.com/gkopff/gson-jodatime-serialisers
 */
class DateTimeDeserializer : JsonDeserializer<DateTime?> {
  @Throws(JsonParseException::class)
  override fun deserialize(jsonElement: JsonElement,
                           typeOfT: Type,
                           context: JsonDeserializationContext): DateTime? {
    if (jsonElement.asString == null || jsonElement.asString.isEmpty()) {
      return null
    }
    val formatter = ISODateTimeFormat.dateTimeParser().withOffsetParsed()
    return formatter.parseDateTime(jsonElement.asString)
  }
}
