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

  @SuppressWarnings("MissingPermission")
  override fun onFinish() {
    performUpdate()
  }

  @SuppressWarnings("MissingPermission")
  fun performUpdate() {
    fusedLocationClient.lastLocation
      .addOnSuccessListener { location ->
        Log.i("dags1", "woot, update viewModel and re-start timer")
        viewModel.performUpdate(location)
        start()
      }
      .addOnFailureListener {
        Log.i("dags1", "drat, failed to get location")
      }
      .addOnCompleteListener {
        Log.i("dags1", "onComplete of getting location")
      }
  }

}
