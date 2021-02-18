package fr.afaucogney.mobile.flipper.internal

import android.graphics.Bitmap
import java.io.Serializable
import java.lang.ref.WeakReference

data class BitmapInfo(
    val bitmapWidth: Int,
    val bitmapHeight: Int,
    var bitmap: WeakReference<Bitmap>,
) : Serializable {
    val isValid: Boolean
        get() = bitmapHeight > 0 && bitmapWidth > 0
    val size: Int
        get() = bitmapHeight * bitmapWidth
    var resId: Int? = null
    var viewParents: Set<String>? = null
}
