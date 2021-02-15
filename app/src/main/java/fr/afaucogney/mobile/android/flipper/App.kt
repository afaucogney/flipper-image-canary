package fr.afaucogney.mobile.android.flipper

import android.app.Application
import com.facebook.flipper.BuildConfig
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import fr.afaucogney.mobile.flipper.FlipperFragmentManagerPlugin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        if (BuildConfig.DEBUG) {
            SoLoader.init(this, false)
//            if (FlipperUtils.shouldEnableFlipper(this)) {
                val client: FlipperClient = AndroidFlipperClient.getInstance(this)
                with(client) {
                    addPlugin(
                        InspectorFlipperPlugin(
                            this@App,
                            DescriptorMapping.withDefaults()
                        )
                    )
                    /**
                     * initialisation of KtpFlipperPlugin with its default constructor
                     */
                    addPlugin(FlipperFragmentManagerPlugin(this@App))
                    start()
                }
//            }
//        }
    }
}