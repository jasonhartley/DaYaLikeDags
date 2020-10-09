package us.jasonh.dayalikedags

import android.os.CountDownTimer
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import us.jasonh.dayalikedags.viewmodel.MainViewModel

class DagsRefresher(
  private val viewModel: MainViewModel,
  private val fusedLocationClient: FusedLocationProviderClient,
  millisInFuture: Long
)
  : CountDownTimer(millisInFuture, millisInFuture) {

  override fun onTick(millisUntilFinished: Long) {
  }

  override fun onFinish() {
    performUpdate()
  }

  @SuppressWarnings("MissingPermission")
  fun performUpdate() {
    fusedLocationClient.lastLocation
      .addOnSuccessListener { location ->
        viewModel.performUpdate(location)
        start()
      }
      .addOnFailureListener {
        Log.e("dags1", "Failed to get last known location")
      }
  }

}
