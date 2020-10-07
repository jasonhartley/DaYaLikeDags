package us.jasonh.dayalikedags

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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

  private lateinit var fullscreenContent: TextView
  private val hideHandler = Handler()

  private val binding by lazy {
    ActivityFullscreenBinding.inflate(
      layoutInflater
    )
  }

  private lateinit var viewModel: MainViewModel

  @SuppressLint("InlinedApi")
  private val hidePart2Runnable = Runnable {
    // Delayed removal of status and navigation bar

    // Note that some of these constants are new as of API 16 (Jelly Bean)
    // and API 19 (KitKat). It is safe to use them, as they are inlined
    // at compile-time and do nothing on earlier devices.
    fullscreenContent.systemUiVisibility =
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

  private var isFullscreen: Boolean = false

  private val hideRunnable = Runnable { hide() }

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(binding.root)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    isFullscreen = true

    // Set up the user interaction to manually show or hide the system UI.
    fullscreenContent = findViewById(R.id.fullscreen_content)
    fullscreenContent.setOnClickListener { toggle() }

    viewModel = MainViewModel(sunriseSunsetApi, dogImageApi, catImageApi)
    viewModel.uiState().observe(this, Observer { uiState ->
      if (uiState != null) {
        render(uiState)
      }
    })
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100)
  }

  override fun onResume() {
    super.onResume()

    val location = Location("")
    location.latitude = 44.123
    location.longitude = -130.123
    viewModel.performUpdate(location)
  }

  private fun render(uiState: UiState) {
    when (uiState) {
      is UiState.Loading -> {
        onLoading()
      }
      is UiState.Success -> {
        onSuccess(uiState)
      }
      is UiState.Error -> {
        onError(uiState)
      }
    }
  }

  private fun onLoading() = with(binding) {
    // maybe just get rid of this
  }

  private fun onSuccess(uiState: UiState.Success) = with(binding) {
    // display the new image
    Log.i("dags", "nice")
    binding.fullscreenContent.text = uiState.imageUrl
  }

  private fun onError(uiState: UiState.Error) = with(binding) {
    toast(uiState.message)
  }

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
    fullscreenContent.systemUiVisibility =
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
