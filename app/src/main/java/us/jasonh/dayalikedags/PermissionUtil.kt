package us.jasonh.dayalikedags

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.core.app.ActivityCompat

class PermissionUtil {
  companion object {

    fun requestLocationPermission(activity: Activity) {
      Log.i("dags1", "requestLocationPermission")
      requestPermission(activity, 13, // TODO: make an enum maybe
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
      )
    }

    private fun requestPermission(activity: Activity, requestCode: Int, permissions: Array<String>) {
      ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
  }

}
