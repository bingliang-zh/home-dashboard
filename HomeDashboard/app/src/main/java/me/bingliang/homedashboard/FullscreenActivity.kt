package me.bingliang.homedashboard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_fullscreen.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mHideRunnable = Runnable { hide() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
    }

    override fun onResume() {
        super.onResume()
        delayedHide(100)
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private val mBackgroundHandler = Handler()

    private fun changeBackground(context: Context, color: Int) {
        fullscreen_content.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun onIdleClick(view: View) {
        changeBackground(view.context, R.color.idle)
        mBackgroundHandler.postDelayed(
            {
                changeBackground(view.context, R.color.default_background)
            }
            , 1000
        )
    }
    fun onActivatedClick(view: View) {
        changeBackground(view.context, R.color.activated)
        mBackgroundHandler.postDelayed(
            {
                changeBackground(view.context, R.color.default_background)
            }
            , 1000
        )
    }
    fun onSuccessClick(view: View) {
        changeBackground(view.context, R.color.success)
        mBackgroundHandler.postDelayed(
            {
                changeBackground(view.context, R.color.default_background)
            }
            , 1000
        )
    }
    fun onFailClick(view: View) {
        changeBackground(view.context, R.color.fail)
        mBackgroundHandler.postDelayed(
            {
                changeBackground(view.context, R.color.default_background)
            }
            , 1000
        )
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
