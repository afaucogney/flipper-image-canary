package fr.afaucogney.mobile.flipper.internal

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import java.util.*

object ThreadUtil {
    private val sLockForHandlerManager = Any()
    private val sHandlerMap: MutableMap<String, Handler> = HashMap()

    fun createIfNotExistHandler(tag: String): Handler {
        synchronized(sLockForHandlerManager) {
            val tmp = sHandlerMap[tag]
            if (tmp != null) {
                return tmp
            }
            val handlerThread = HandlerThread(tag, Process.THREAD_PRIORITY_BACKGROUND)
            handlerThread.start()
            val handler = Handler(handlerThread.looper)
            sHandlerMap[tag] = handler
            return handler
        }
    }
}