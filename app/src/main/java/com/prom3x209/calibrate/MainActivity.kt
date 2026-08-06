package com.prom3x209.calibrate

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setDecorFitsSystemWindows(false)

        val text = TextView(this).apply {
            textSize = 16f
            setPadding(40, 80, 40, 40)
            setTextIsSelectable(true)
        }
        val scroll = ScrollView(this)
        scroll.addView(text)
        setContentView(scroll)

        window.decorView.setOnApplyWindowInsetsListener { _, insets ->
            text.text = buildReport(insets)
            insets
        }
        window.decorView.post {
            window.decorView.rootWindowInsets?.let { text.text = buildReport(it) }
        }
    }

    private fun buildReport(insets: WindowInsets): String {
        val sb = StringBuilder()
        sb.append("Rotate the phone to check both orientations.\n\n")
        sb.append("Screen: ${resources.displayMetrics.widthPixels} x ${resources.displayMetrics.heightPixels} px, density ${resources.displayMetrics.density}\n\n")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val positions = mapOf(
                "TOP_LEFT" to WindowInsets.RoundedCorner.POSITION_TOP_LEFT,
                "TOP_RIGHT" to WindowInsets.RoundedCorner.POSITION_TOP_RIGHT,
                "BOTTOM_LEFT" to WindowInsets.RoundedCorner.POSITION_BOTTOM_LEFT,
                "BOTTOM_RIGHT" to WindowInsets.RoundedCorner.POSITION_BOTTOM_RIGHT
            )
            for ((name, pos) in positions) {
                val rc = insets.getRoundedCorner(pos)
                if (rc != null) {
                    sb.append("$name: radius=${rc.radius}px center=${rc.center}\n")
                } else {
                    sb.append("$name: null (device reports no rounded corner here)\n")
                }
            }
        } else {
            sb.append("Android version too old for getRoundedCorner() API (needs 12+).\n")
        }

        sb.append("\n")
        val cutout = insets.displayCutout
        if (cutout != null) {
            sb.append("Cutout safe insets: L=${cutout.safeInsetLeft} T=${cutout.safeInsetTop} R=${cutout.safeInsetRight} B=${cutout.safeInsetBottom}\n")
            sb.append("Cutout bounding rects: ${cutout.boundingRects}\n")
        } else {
            sb.append("No display cutout reported.\n")
        }

        return sb.toString()
    }
}
