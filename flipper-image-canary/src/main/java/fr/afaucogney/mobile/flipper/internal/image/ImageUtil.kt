package fr.afaucogney.mobile.flipper.internal.image

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.util.Base64
import androidx.annotation.VisibleForTesting
import java.io.ByteArrayOutputStream

object ImageUtil {
    fun convertToBase64(bitmap: Bitmap?, maxWidth: Int, maxHeight: Int): String? {
        if (bitmap == null) {
            return null
        }
        val startTime = System.currentTimeMillis()
        val targetSize = computeTargetSize(bitmap, maxWidth, maxHeight)
        // 10-100ms量级
        val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, targetSize[0], targetSize[1])
        val baos = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos)
        //        L.d("ImageUtil.convertToBase64 cost %s ms", (System.currentTimeMillis() - startTime));
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }

    @VisibleForTesting
    fun computeTargetSize(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): IntArray {
        val width = bitmap.width
        val height = bitmap.height
        var targetWidth = width.toFloat()
        var targetHeight = height.toFloat()
        if (width > maxWidth || height > maxHeight) {
            val scaleWidth = width * 1.0f / maxWidth
            val scaleHeight = height * 1.0f / maxHeight
            val scale = Math.max(scaleWidth, scaleHeight)
            targetWidth = width / scale
            targetHeight = height / scale
        }
        return intArrayOf(targetWidth.toInt(), targetHeight.toInt())
    }
}