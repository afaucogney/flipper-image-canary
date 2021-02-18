package fr.afaucogney.mobile.flipper.internal

import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import fr.afaucogney.mobile.android.flipper.R
import java.lang.ref.WeakReference
import java.util.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

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
                ).apply {
                    resId = view.getResourceId()
                    viewParents = view.getViewParents()
                }
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

    fun ImageView.getResourceId(): Int {
        return this.getPrivateProperty<ImageView, Int>("mResource") ?: -1
    }

    ///////////////////////////////////////////////////////////////////////////
    // REFLEXION HELPER
    ///////////////////////////////////////////////////////////////////////////

    private inline fun <reified T : Any, R> T.getPrivateProperty(name: String): R? =
        T::class
            .memberProperties
            .firstOrNull { it.name == name }
            ?.apply { isAccessible = true }
            ?.get(this) as? R
}

private fun ImageView.getViewParents(): Set<String> {
    var result = mutableSetOf<String>()
    result.add(this::class.java.simpleName)
    var start = this as View
    while (start != this.rootView) {
        result.add(start::class.java.simpleName + " - id: " + start.id)
        start = start.parent as View
    }
    return result
}
