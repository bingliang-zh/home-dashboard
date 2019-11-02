package me.bingliang.homedashboard

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_fullscreen.*

import ai.picovoice.porcupinemanager.KeywordCallback
import ai.picovoice.porcupinemanager.PorcupineManager
import ai.picovoice.porcupinemanager.PorcupineManagerException
import android.os.CountDownTimer
import android.widget.ToggleButton
import java.io.File

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    private var porcupineManager: PorcupineManager? = null
    private var notificationPlayer: MediaPlayer? = null
    private var recordButton: ToggleButton? = null
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
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
        Utils.configurePorcupine(this)
        notificationPlayer = MediaPlayer.create(this, R.raw.notification)
        recordButton = findViewById(R.id.btn_record)
    }

    override fun onResume() {
        super.onResume()
        delayedHide(100)
    }

    private fun hide() {
        supportActionBar?.hide()
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private val mBackgroundHandler = Handler()

    private fun simpleChangeBackground(color: Int) {
        fullscreen_content.setBackgroundColor(ContextCompat.getColor(fullscreen_content.context, color))
    }
    private fun changeBackground(color: Int) {
        simpleChangeBackground(color)
        mBackgroundHandler.removeCallbacksAndMessages(null)
        mBackgroundHandler.postDelayed({ simpleChangeBackground(R.color.default_background) }, 1000)
    }

    fun onIdleClick(view: View) { changeBackground(R.color.idle) }
    fun onActivatedClick(view: View) { changeBackground(R.color.activated) }
    fun onSuccessClick(view: View) { changeBackground(R.color.success) }
    fun onFailClick(view: View) { changeBackground(R.color.fail) }

    fun onRecordClick(view: View) {
        try {
            if (recordButton!!.isChecked) {
                // check if record permission was given.
                if (Utils.hasRecordPermission(this)) {
                    porcupineManager = initPorcupine()
                    porcupineManager?.start()

                } else {
                    Utils.showRecordPermission(this)
                }
            } else {
                porcupineManager?.stop()
            }
        } catch (e: PorcupineManagerException) {
            Utils.showErrorToast(this)
        }

    }

    @Throws(PorcupineManagerException::class)
    private fun initPorcupine(): PorcupineManager {
        val kwd = "Hey Pico"
        val filename = kwd.toLowerCase().replace("\\s+".toRegex(), "_")
        val keywordFilePath = File(this.filesDir, "${filename}.ppn")
            .absolutePath
        val modelFilePath = File(this.filesDir, "params.pv").absolutePath
        return PorcupineManager(modelFilePath, keywordFilePath, 0.2f, KeywordCallback {
            runOnUiThread {
                if (!notificationPlayer!!.isPlaying) {
                    notificationPlayer?.start()
                }

                simpleChangeBackground(R.color.success)
                object : CountDownTimer(1000, 100) {

                    override fun onTick(millisUntilFinished: Long) {
                        if (!notificationPlayer!!.isPlaying) {
                            notificationPlayer?.start()
                        }
                    }

                    override fun onFinish() {
                        simpleChangeBackground(R.color.default_background)
                    }
                }.start()
            }
        })
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        private val UI_ANIMATION_DELAY = 300
    }
}
