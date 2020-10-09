package us.jasonh.dayalikedags

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import us.jasonh.dayalikedags.api.CatImageApi
import us.jasonh.dayalikedags.api.DogImageApi
import us.jasonh.dayalikedags.api.SunriseSunsetApi
import us.jasonh.dayalikedags.databinding.ActivityFullscreenBinding
import us.jasonh.dayalikedags.viewmodel.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FullscreenActivity : AppCompatActivity() {
  @Inject lateinit var sunriseSunsetApi: SunriseSunsetApi
  @Inject lateinit var dogImageApi: DogImageApi
  @Inject lateinit var catImageApi: CatImageApi

  private lateinit var viewModel: MainViewModel
  private lateinit var dagsRefresher: DagsRefresher
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private val hideHandler = Handler()
  private val hideRunnable = Runnable { hide() }
  private var isFullscreen: Boolean = false

  private val binding by lazy {
    ActivityFullscreenBinding.inflate(
      layoutInflater
    )
  }

  @SuppressLint("InlinedApi")
  private val hidePart2Runnable = Runnable {
    // Delayed removal of status and navigation bar

    // Note that some of these constants are new as of API 16 (Jelly Bean)
    // and API 19 (KitKat). It is safe to use them, as they are inlined
    // at compile-time and do nothing on earlier devices.
    binding.image.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  }

  private val showPart2Runnable = Runnable {
    // Delayed display of UI elements
    supportActionBar?.show()
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(binding.root)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    isFullscreen = true

    // Set up the user interaction to manually show or hide the system UI.
    binding.image.setOnClickListener { toggle() }

    viewModel = MainViewModel(sunriseSunsetApi, dogImageApi, catImageApi)
    viewModel.uiState().observe(this, { uiState ->
      if (uiState != null) {
        render(uiState)
      }
    })

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    dagsRefresher = DagsRefresher(viewModel, fusedLocationClient, 300000)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100)
  }

  override fun onStart() {
    super.onStart()
    Log.i("dags1", "onStart()")
    if (isLocationPermissionGranted(this)) {
      Log.i("dags1", "..location permission granted, start showing dags")
      dagsRefresher.performUpdate()
    } else {
      Log.i("dags1", "..location permission NOT granted, ask for it")
      requestLocationPermission(this)
    }
  }

  override fun onStop() {
    super.onStop()
    Log.i("dags1", "onStop()")
    dagsRefresher.cancel()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String?>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      RequestCode.LOCATION_PERMISSION -> {
        var permissionsAsSingleString = ""
        permissions.forEach { permissionsAsSingleString += it }
        var grantResultsAsString = "size: ${grantResults.size}, values: "
        grantResults.forEach { grantResultsAsString += it.toString() }
        Log.i("dags1", "onRequestPermissionsResult RequestCode.LOCATION_PERMISSION, permissions: $permissionsAsSingleString, grantResults: $grantResultsAsString")
        if (grantResults.contains(0)) {
          dagsRefresher.performUpdate()
        } else {
          toast("No location, no dags!")
        }
      }
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  private fun render(uiState: UiState) {
    when (uiState) {
      is UiState.Success -> {
        onSuccess(uiState)
      }
      is UiState.Error -> {
        onError(uiState)
      }
    }
  }

  private fun onSuccess(uiState: UiState.Success) = with(binding) {
    val message = "Location: ${uiState.location.latitude}, ${uiState.location.longitude}\n" +
        "${uiState.nextSolarEvent}"
    binding.info.text = message

    if (uiState.imageUrl.endsWith("gif")) {
      Glide.with(baseContext).asGif().load(uiState.imageUrl).into(binding.image)
    } else {
      Glide.with(baseContext).load(uiState.imageUrl).into(binding.image)
    }
  }

  private fun onError(uiState: UiState.Error) = toast(uiState.message)

  private fun toggle() {
    if (isFullscreen) {
      hide()
    } else {
      show()
    }
  }

  private fun hide() {
    // Hide UI first
    supportActionBar?.hide()
    isFullscreen = false

    // Schedule a runnable to remove the status and navigation bar after a delay
    hideHandler.removeCallbacks(showPart2Runnable)
    hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
  }

  private fun show() {
    // Show the system bar
    binding.image.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    isFullscreen = true

    // Schedule a runnable to display UI elements after a delay
    hideHandler.removeCallbacks(hidePart2Runnable)
    hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
  }

  /**
   * Schedules a call to hide() in [delayMillis], canceling any
   * previously scheduled calls.
   */
  private fun delayedHide(delayMillis: Int) {
    hideHandler.removeCallbacks(hideRunnable)
    hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
  }

  private fun isLocationPermissionGranted(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED)
  }

  private fun requestLocationPermission(activity: Activity) {
    PermissionUtil.requestLocationPermission(activity)
  }

  companion object {
    /**
     * Whether or not the system UI should be auto-hidden after
     * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
     */
    private const val AUTO_HIDE = true

    /**
     * If [AUTO_HIDE] is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private const val AUTO_HIDE_DELAY_MILLIS = 3000

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private const val UI_ANIMATION_DELAY = 300
  }
}
