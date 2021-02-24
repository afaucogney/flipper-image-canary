package fr.afaucogney.mobile.flipper

import DefaultImageCanaryConfigProvider
import android.app.Activity
import android.app.Application
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnDrawListener
import androidx.annotation.VisibleForTesting
import com.facebook.flipper.core.FlipperArray
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import fr.afaucogney.mobile.flipper.internal.*
import fr.afaucogney.mobile.flipper.internal.image.ImageUtil
import java.lang.ref.WeakReference


class ImageCanaryFlipperPlugin(val app: Application) :
    Application.ActivityLifecycleCallbacks,
    FlipperPlugin {

    init {
        app.registerActivityLifecycleCallbacks(this)
    }

    ///////////////////////////////////////////////////////////////////////////
    // DATA
    ///////////////////////////////////////////////////////////////////////////

    private var connection: FlipperConnection? = null

    ///////////////////////////////////////////////////////////////////////////
    // SPECIALIZATION
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Setup the unique id of the plugin
     */
    override fun getId(): String {
        return "ImageCanaryFlipper"
    }

    /**
     * onConnect is triggered every time the plugin is shown on Flipper
     * It does keep the connection
     * And parse Ktp scope tree to then push it to the Desktop Flipper Client
     */
    override fun onConnect(connection: FlipperConnection?) {
        this.connection = connection
//        activityMap.forEach { (_, u) -> connection?.send("newData", u.build()) }
        issues.build().send()
    }

    /**
     * Release the connection
     */
    override fun onDisconnect() {
        connection = null
    }

    /**
     * Plugin doe run in background
     */
    override fun runInBackground(): Boolean {
        return false
    }

    ///////////////////////////////////////////////////////////////////////////
    // FLIPPER
    ///////////////////////////////////////////////////////////////////////////

    companion object {
        const val NEW_DATA = "newData"
    }

    private fun FlipperObject.send() {
        this.apply { connection?.send(NEW_DATA, this) }
    }

    private fun FlipperArray.send() {
        this.apply { connection?.send(NEW_DATA, this) }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ACTIVITY
    ///////////////////////////////////////////////////////////////////////////

    private val IMAGE_CANARY_HANDLER = "godeye-imagecanary"
    var handler: Handler = ThreadUtil.createIfNotExistHandler(IMAGE_CANARY_HANDLER)
    private val mOnDrawListenerMap = mutableMapOf<Activity, OnDrawListener>()
    private val mImageIssues = hashSetOf<ImageIssue>()
    private val bitmapInfoAnalyzer: BitmapInfoAnalyzer = DefaultBitmapInfoAnalyzer()
    private val mImageCanaryConfigProvider: ImageCanaryConfigProvider =
        DefaultImageCanaryConfigProvider()

    override fun onActivityResumed(activity: Activity) {
        val parent = activity.window.decorView as ViewGroup
        val callback: Runnable = inspectInner(
            WeakReference(activity),
//            imageCanaryEngine,
            mImageIssues
        )
        val onDrawListener = OnDrawListener {
            if (handler != null) {
                handler.removeCallbacks(callback)
                handler.postDelayed(callback, 300)
            }
        }
        mOnDrawListenerMap[activity] = onDrawListener
        parent.viewTreeObserver.addOnDrawListener(onDrawListener)
    }

    override fun onActivityPaused(activity: Activity) {
        mOnDrawListenerMap.remove(activity)?.run {
            val parent = activity.window.decorView as ViewGroup
            parent.viewTreeObserver.removeOnDrawListener(this)
        }
    }

    @VisibleForTesting
    fun inspectInner(
        activity: WeakReference<Activity?>,
//        imageCanaryEngine: ImageCanary,
        imageIssues: MutableSet<ImageIssue>
    ): Runnable {
        return Runnable {
            try {
                val p = activity.get()
                if (p != null) {
                    val parent = p.window.decorView as ViewGroup
                    recursiveLoopChildren(
                        p,
                        parent,
//                        imageCanaryEngine,
                        imageIssues
                    )
                }
            } catch (e: Throwable) {
                Log.e("inspectInner", e.message, e)
            }
        }
    }

    private fun recursiveLoopChildren(
        activity: Activity,
        parent: ViewGroup,
//        imageCanaryEngine: ImageCanary,
        imageIssues: MutableSet<ImageIssue>
    ) {

        ViewUtil.getChildren(
            parent,
            object : ViewUtil.ViewFilter {
                override fun isExclude(view: View?): Boolean {
                    return false
                }
            },
            object : ViewUtil.ViewProcess {
                override fun onViewProcess(view: View?) {
                    view?.let {
                        val bitmapInfos: List<BitmapInfo> = bitmapInfoAnalyzer.analyze(it)
                        for (bitmapInfo in bitmapInfos) {
                            if (bitmapInfo.isValid) {
                                val imageIssue = ImageIssue(
                                    System.currentTimeMillis(),
                                    activity.javaClass.name,
                                    activity.hashCode(),
                                    view.hashCode(),
                                    bitmapInfo.bitmapHeight,
                                    bitmapInfo.bitmapWidth,
                                    view.width,
                                    view.height,
                                    calcIssueType(view, bitmapInfo)
                                )
                                if (imageIssue.issueType != IssueType.NONE && !imageIssues.contains(
                                        imageIssue
                                    )
                                ) {
                                    imageIssues.add(imageIssue.copy())
                                    imageIssue.imageSrcBase64 =
                                        ImageUtil.convertToBase64(bitmapInfo.bitmap.get(), 200, 200)
                                    bitmapInfo.resId?.let {
                                        if (it > 0) {
                                            imageIssue.resourceId = it
                                            imageIssue.resourcePath = getResourcePath(it)
                                        }
                                    }
                                    bitmapInfo.bitmap.get()?.let {
                                        imageIssue.allocatedByteCount =
                                            it.allocationByteCount / (1000000)
                                        imageIssue.byteCount = it.byteCount / (1000000)
                                    }
                                    imageIssue.viewParents =
                                        bitmapInfo.viewParents?.toList() ?: listOf()
                                    imageIssue.toFO().build().addAndSend()
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    private fun calcIssueType(view: View, bitmapInfo: BitmapInfo): IssueType {
        return when {
            view.visibility != View.VISIBLE -> IssueType.INVISIBLE_BUT_MEMORY_OCCUPIED
            mImageCanaryConfigProvider.isBitmapQualityTooHigh(
                bitmapInfo.bitmapWidth,
                bitmapInfo.bitmapHeight,
                view.width,
                view.height
            ) -> IssueType.BITMAP_QUALITY_TOO_HIGH
            mImageCanaryConfigProvider.isBitmapQualityTooLow(
                bitmapInfo.bitmapWidth,
                bitmapInfo.bitmapHeight,
                view.width,
                view.height
            ) -> IssueType.BITMAP_QUALITY_TOO_LOW
            else -> IssueType.NONE
        }
    }


    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }


    private fun ImageIssue.toFO(): FlipperObject.Builder {
        return FlipperObject
            .Builder()
            .put("activityClass", this.activityClassName)
            .put("activityHash", this.activityHashCode)
            .put("bitmapHeight", this.bitmapHeight)
            .put("bitmapWidth", bitmapWidth)
            .put("base64", imageSrcBase64)
            .put("imageViewHash", imageViewHashCode)
            .put("imageViewHeight", imageViewHeight)
            .put("imageViewWidth", imageViewWidth)
            .put("issueType", issueType)
//            .put("resourRESOURCE_ID", resourceId)
//            .put("RESOURCE_PATH", resourcePath)
            .put("byteCount", byteCount)
            .put("allocatedByteCount", allocatedByteCount)
            .put("viewParents", viewParents.toFlipperObjectTree())

    }

    val issues = FlipperArray.Builder()

    private fun FlipperObject.addAndSend() {
        issues.put(this)
        issues.build().send()
//        FlipperObject.Builder()
//            .put("tutu",issues.build())
//            .build()
//            .send()
    }

    fun getResourcePath(id: Int): String {
        try {

            val value = TypedValue()
            app.resources.getValue(id, value, true)
            // check value.string if not null - it is not null
            return value.string.toString()
        } catch (e: Resources.NotFoundException) {
            return ""
        }
    }
}

private fun List<String>.toFlipperObjectTree(): FlipperObject.Builder {
    var result = FlipperObject.Builder()
    val init = this.size - 1

    fun recurse(index: Int, temp: FlipperObject.Builder?) {
        if (index < 0) {
            result = temp!!
            return
        } else {
            if (index == init) {
                recurse(index - 2, FlipperObject.Builder().put(this[index - 1], this[index]))
            } else {
                recurse(index - 1, FlipperObject.Builder().put(this[index], temp))
            }
        }

    }
    recurse(init, null)
    return result
}
