package fr.afaucogney.mobile.flipper

import DefaultImageCanaryConfigProvider
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnDrawListener
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.facebook.flipper.core.FlipperArray
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import fr.afaucogney.mobile.flipper.internal.*
import fr.afaucogney.mobile.flipper.internal.image.ImageUtil
import java.lang.ref.WeakReference


class ImageCanaryFlipperPlugin(app: Application) :
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
//
//    ///////////////////////////////////////////////////////////////////////////
//    // FLIPPER
//    ///////////////////////////////////////////////////////////////////////////
//
    companion object {
//        const val FID = "id"
//        const val NAME = "name"
//        const val FULL_NAME = "fullName"
//        const val FRAGMENTS = "fragments"
//        const val TYPE = "type"
//        const val LIFE_CYCLE_EVENT = "lifeCycle"
//        const val BACK_STACK = "backStack"
        const val NEW_DATA = "newData"
//        const val TRASH = "trash"
    }
//
//    private enum class FlipperObjectType {
//        ACTIVITY,
//        FRAGMENT
//    }
//
//    private val FlipperObjectType.key: String
//        get() = this.toString().toLowerCase()
//
//    ///////////////////////////////////////////////////////////////////////////
//    // DOMAIN
//    ///////////////////////////////////////////////////////////////////////////
//
//    // Activity
//    private val Activity.name: String
//        get() = this.javaClass.simpleName
//
//    private val Activity.fullName: String
//        get() = this.toString()
//
//    private val Activity.fid: String
//        get() = this.fullName.split("@")[1]
//
//    private val Activity.type: String
//        get() = FlipperObjectType.ACTIVITY.key
//
//    private enum class ActivityLifeCycle {
//        ON_ACTIVITY_CREATED,
//        ON_ACTIVITY_STARTED,
//        ON_ACTIVITY_RESUMED,
//        ON_ACTIVITY_PAUSED,
//        ON_ACTIVITY_STOPPED,
//        ON_ACTIVITY_SAVE_INSTANCE_STATE,
//        ON_ACTIVITY_DESTROYED
//    }
//
//    private val ActivityLifeCycle.key: String
//        get() = this.toString().toLowerCase()
//
//    // Fragment
//    private val Fragment.name: String
//        get() = this.javaClass.simpleName
//
//    private val Fragment.fullName: String
//        get() = this.toString()
//
//    private val Fragment.fid: String
//        get() = this.fullName.split("{")[1].split("}")[0]
//
//    private val Fragment.type: String
//        get() = FlipperObjectType.FRAGMENT.key
//
//    private enum class FragmentLifeCycle {
//        ON_FRAGMENT_ATTACHED,
//        ON_FRAGMENT_CREATED,
//        ON_FRAGMENT_VIEW_CREATED,
//        ON_FRAGMENT_ACTIVITY_CREATED,
//        ON_FRAGMENT_STARTED,
//        ON_FRAGMENT_RESUMED,
//        ON_FRAGMENT_PAUSED,
//        ON_FRAGMENT_STOPPED,
//        ON_FRAGMENT_SAVE_INSTANCE_STATE,
//        ON_FRAGMENT_VIEW_DESTROYED,
//        ON_FRAGMENT_DESTROYED,
//        ON_FRAGMENT_DETACHED,
//    }
//
//    private val FragmentLifeCycle.key: String
//        get() = this.toString().toLowerCase()
//
//    private val trashMap = FlipperArray.Builder()
//    private val activityMap = mutableMapOf<String, FlipperObject.Builder>()
//
//    //    private val fragmentMap = mutableMapOf<String, Map<String, FlipperObject.Builder>>()
//    private val fragmentMap = mutableMapOf<String, HashMap<String, FlipperObject.Builder>>()
//
//    private val backStackListener = FragmentManager.OnBackStackChangedListener {
//
//    }
//
//    ///////////////////////////////////////////////////////////////////////////
//    // FLIPPER TRANSMISSION
//    ///////////////////////////////////////////////////////////////////////////
//
//    private fun pushActivityEvent(
//        activity: Activity,
//        event: ActivityLifeCycle
//    ) {
//        activity.saveAndMapToFlipperObjectBuilder(event)
//            .build()
//            .send()
//    }
//
//    private fun pushFragmentEvent(
//        fragment: Fragment,
//        event: FragmentLifeCycle
//    ) {
//        fragment.saveAndMapToFlipperObjectBuilder(event)
//            .build()
//            .send()
//    }

    private fun FlipperObject.send() {
        this.apply { connection?.send(NEW_DATA, this) }
    }

    private fun FlipperArray.send() {
        this.apply { connection?.send(NEW_DATA, this) }
    }
//
//    private fun Fragment.moveToTrash() {
//        trashMap.put(fragmentMap[this.name]!![this.fid])
//        fragmentMap[this.name]!!.remove(this.fid)
//        this.requireActivity()
//    }
//
//    ///////////////////////////////////////////////////////////////////////////
//    // ACTIVITY HELPER
//    ///////////////////////////////////////////////////////////////////////////
//
//    private fun Activity.toFlipperObjectBuilder(): FlipperObject.Builder {
//        return FlipperObject.Builder()
//            .put(FID, this.fid)
//            .put(NAME, this.name)
//            .put(FULL_NAME, this.fullName)
//            .put(TYPE, FlipperObjectType.ACTIVITY.key)
//    }
//
//    private fun FlipperObject.Builder.addLifeCycleEvent(event: ActivityLifeCycle?): FlipperObject.Builder {
//        return if (event != null)
//            this.put(LIFE_CYCLE_EVENT, event.key)
//        else this
//    }
//
//    private fun FlipperObject.Builder.addBackStackInfo(activity: Activity): FlipperObject.Builder {
//        return this.apply {
//            if (activity is FragmentActivity) {
//                val backStack = FlipperObject.Builder()
//                for (i in 0 until activity.supportFragmentManager.backStackEntryCount) {
//                    val entry = activity.supportFragmentManager.getBackStackEntryAt(i)
//                    backStack.put(entry.id.toString(), entry.name)
//                }
//                put(BACK_STACK, backStack)
//            }
//        }
//    }
//
//    private fun Activity.saveAndMapToFlipperObjectBuilder(event: ActivityLifeCycle? = null): FlipperObject.Builder {
//        if (!activityMap.containsKey(this.fid)) {
//            activityMap[this.fid] = this.toFlipperObjectBuilder()
//        }
//        return activityMap[this.fid]!!
//            .addLifeCycleEvent(event)
//            .addBackStackInfo(this)
//            .put(FRAGMENTS, fragmentMap.toFO())
//            .let {
//                FlipperObject.Builder()
//                    .put(this.fid, it)
//
//            }
//            .let {
//                FlipperObject.Builder()
//                    .put(this.name, it)
//                    .put(TRASH, trashMap)
//            }
//    }
//
//    private fun Activity.toFlipperObject(event: ActivityLifeCycle? = null): FlipperObject.Builder {
//        if (!activityMap.containsKey(this.fid)) {
//            activityMap[this.fid] = this.toFlipperObjectBuilder()
//        }
//        return activityMap[this.fid]!!
//            .addLifeCycleEvent(event)
//            .addBackStackInfo(this)
//            .put(FRAGMENTS, fragmentMap.toFO())
//            .let {
//                FlipperObject.Builder()
//                    .put(this.fid, it)
//
//            }
//            .let {
//                FlipperObject.Builder()
//                    .put(this.name, it)
//                    .put(TRASH, trashMap)
//            }
//    }

    ///////////////////////////////////////////////////////////////////////////
    // FRAGMENT HELPER
    ///////////////////////////////////////////////////////////////////////////
//
//    private fun Fragment.toFlipperObjectBuilder(): FlipperObject.Builder {
//        return FlipperObject.Builder()
//            .put(FID, this.fid)
//            .put(NAME, this.name)
//            .put(FULL_NAME, this.fullName)
//            .put(TYPE, FlipperObjectType.FRAGMENT.key)
//    }
//
//    private fun FlipperObject.Builder.addLifeCycleEvent(event: FragmentLifeCycle): FlipperObject.Builder {
//        return this.put(LIFE_CYCLE_EVENT, event.key)
//    }
//
//    @SuppressLint("RestrictedApi")
//    private fun FlipperObject.Builder.addNavBackStack(fragment: Fragment): FlipperObject.Builder {
//        return this
//            .let {
//                if (fragment.name == "NavHostFragment") {
//                    try {
//                        val result = FlipperObject.Builder()
//                        fragment.findNavController()
//                            .backStack
//                            .forEachIndexed { index, navBackStackEntry ->
//                                result.put(
//                                    index.toString(),
//                                    navBackStackEntry.destination.displayName
//                                )
//                            }
//                        it.put(BACK_STACK, result)
//                    } catch (e: IllegalStateException) {
//                        it
//                    }
//                } else {
//                    it
//                }
//            }
//    }
//
//    @SuppressLint("RestrictedApi")
//    private fun Fragment.saveAndMapToFlipperObjectBuilder(event: FragmentLifeCycle): FlipperObject.Builder {
//        return this.toFlipperObjectBuilder()
//            .addLifeCycleEvent(event)
//            .addNavBackStack(this)
//            .also { builder ->
//                if (!fragmentMap.containsKey(this.name)) {
//                    fragmentMap[this.name] = hashMapOf(this.fid to builder)
//                } else {
//                    fragmentMap[this.name]!![this.fid] = builder
//                }
//            }
//            .let {
//                this.requireActivity()
//                    .saveAndMapToFlipperObjectBuilder()
//            }
//    }
//
//    private fun MutableMap<String, HashMap<String, FlipperObject.Builder>>.toFO(): FlipperObject {
//        val result = FlipperObject.Builder()
//        this.toSortedMap().forEach { (t, u) ->
//            val f = FlipperObject.Builder()
//            u.toSortedMap().forEach {
//                f.put(it.key, it.value)
//            }
//            result.put(t, f)
//        }
//        return result.build()
//    }


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


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
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
        return FlipperObject.Builder()
            .put("ACTIVITY_CLASS", this.activityClassName)
            .put("ACTIVITY_HASH", this.activityHashCode)
            .put("BITMAP_HEIGHT", this.bitmapHeight)
            .put("BITMAP_WITH", bitmapWidth)
//            .put("BASE_64", imageSrcBase64)
            .put("IV_HASH", imageViewHashCode)
            .put("IV_HEIGHT", imageViewHeight)
            .put("IV_WIDTH", imageViewWidth)
            .put("ISSUE_TYPE", issueType)
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
}