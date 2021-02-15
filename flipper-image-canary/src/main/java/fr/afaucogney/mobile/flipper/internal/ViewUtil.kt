package fr.afaucogney.mobile.flipper.internal

import android.view.View
import android.view.ViewGroup
import java.util.*

object ViewUtil {
//    fun measureActivityDidDraw(activity: Activity, onDrawCallback: OnDrawCallback) {
//        measurePageDidDraw(activity.window.decorView, onDrawCallback)
//    }
//
//    fun measureFragmentV4DidDraw(fragment: Fragment, onDrawCallback: OnDrawCallback) {
//        val view = fragment.view
//        if (view != null) {
//            measurePageDidDraw(view, onDrawCallback)
//        }
//    }
//
//    fun measureFragmentDidDraw(fragment: android.app.Fragment, onDrawCallback: OnDrawCallback) {
//        val view = fragment.view
//        if (view != null) {
//            measurePageDidDraw(view, onDrawCallback)
//        }
//    }
//
//    private fun measurePageDidDraw(view: View, onDrawCallback: OnDrawCallback) {
//        PageDrawMonitor.newInstance(view, onDrawCallback).listen()
//    }

//    /**
//     * get all views in parent
//     *
//     * @param parent
//     */
//    fun getChildren(parent: ViewGroup): List<View> {
//        val viewFilter = ViewFilter { view: View? -> false }
//        val viewProcess = ViewProcess { view: View? -> }
//        return getChildren(parent, viewFilter, viewProcess)
//    }

    /**
     * get all views in parent exclude viewFilter
     *
     * @param parent
     * @param viewFilter
     * @return
     */
    fun getChildren(
        parent: ViewGroup,
        viewFilter: ViewFilter,
        viewProcess: ViewProcess
    ): List<View> {
        val tmp: MutableList<View> = ArrayList()
        getChildrenRecursive(tmp, parent, viewFilter, viewProcess)
        return tmp
    }

    /**
     * get all views in parent
     *
     * @param allViews
     * @param parent
     * @param viewFilter
     */
    private fun getChildrenRecursive(
        allViews: MutableList<View>,
        parent: ViewGroup,
        viewFilter: ViewFilter,
        viewProcess: ViewProcess
    ) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (!viewFilter.isExclude(child)) {
                allViews.add(child)
                viewProcess.onViewProcess(child)
            }
            if (child is ViewGroup) {
                getChildrenRecursive(allViews, child, viewFilter, viewProcess)
            }
        }
    }

//    interface OnDrawCallback {
//        fun didDraw()
//    }

    interface ViewProcess {
        fun onViewProcess(view: View?)
    }

    interface ViewFilter {
        fun isExclude(view: View?): Boolean
    }
}