package us.jasonh.dayalikedags

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

class PermissionUtil {
  companion object {

    fun requestLocationPermission(activity: Activity) {
      requestPermission(activity, RequestCode.LOCATION_PERMISSION,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
      )
    }

    private fun requestPermission(activity: Activity, requestCode: Int, permissions: Array<String>) {
      ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
  }

}
