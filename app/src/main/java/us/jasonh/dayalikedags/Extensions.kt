package us.jasonh.dayalikedags

import android.content.Context
import android.widget.Toast

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

