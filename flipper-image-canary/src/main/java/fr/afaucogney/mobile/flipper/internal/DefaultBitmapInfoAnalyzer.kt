package fr.afaucogney.mobile.flipper.internal

import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import java.lang.ref.WeakReference

class DefaultBitmapInfoAnalyzer : BitmapInfoAnalyzer {
    override fun analyze(view: View): List<BitmapInfo> {
        var ivBitMapInfo: BitmapInfo? = null
        if (view is ImageView) {
            val drawable = view.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                ivBitMapInfo = BitmapInfo(
                    bitmap.width,
                    bitmap.height,
                    WeakReference(bitmap)
                )
            }
        }
        val vBitMapInfo: BitmapInfo
        val drawable = view.background
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            vBitMapInfo = BitmapInfo(
                bitmap.width,
                bitmap.height,
                WeakReference(bitmap)
            )
            return if (ivBitMapInfo != null) {
                listOf(ivBitMapInfo, vBitMapInfo)
            } else listOf(vBitMapInfo)
        }
        return if (ivBitMapInfo != null) {
            listOf(ivBitMapInfo)
        } else emptyList()
    }
}