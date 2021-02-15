import fr.afaucogney.mobile.flipper.ImageCanaryConfigProvider

class DefaultImageCanaryConfigProvider : ImageCanaryConfigProvider {
    override fun isBitmapQualityTooHigh(
        bitmapWidth: Int,
        bitmapHeight: Int,
        imageViewWidth: Int,
        imageViewHeight: Int
    ): Boolean {
        return bitmapWidth * bitmapHeight > imageViewWidth * imageViewHeight * 1.5
    }

    override fun isBitmapQualityTooLow(
        bitmapWidth: Int,
        bitmapHeight: Int,
        imageViewWidth: Int,
        imageViewHeight: Int
    ): Boolean {
        return bitmapWidth * bitmapHeight * 2 < imageViewWidth * imageViewHeight
    }
}