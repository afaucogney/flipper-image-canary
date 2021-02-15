package fr.afaucogney.mobile.flipper.internal

import android.view.View

interface BitmapInfoAnalyzer {
    fun analyze(view: View): List<BitmapInfo>
}