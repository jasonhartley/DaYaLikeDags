package us.jasonh.dayalikedags

import android.content.Context
import android.widget.Toast
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
  this?.let { Toast.makeText(it, text, duration).show() }

fun DateTime.toUserLocalTime(): String {
  val formatter = DateTimeFormat.forPattern("h:mm a").withZone(DateTimeZone.getDefault())
  return formatter.print(this)
}
