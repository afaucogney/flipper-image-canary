package fr.afaucogney.mobile.flipper

interface ImageCanaryConfigProvider {
    fun isBitmapQualityTooHigh(
        bitmapWidth: Int,
        bitmapHeight: Int,
        imageViewWidth: Int,
        imageViewHeight: Int
    ): Boolean

    fun isBitmapQualityTooLow(
        bitmapWidth: Int,
        bitmapHeight: Int,
        imageViewWidth: Int,
        imageViewHeight: Int
    ): Boolean
}