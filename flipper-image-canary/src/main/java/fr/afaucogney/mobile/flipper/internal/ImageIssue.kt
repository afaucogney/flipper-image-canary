package fr.afaucogney.mobile.flipper.internal

import java.io.Serializable

data class ImageIssue(
    val timestamp: Long,
    val activityClassName: String,
    val activityHashCode: Int,
    val imageViewHashCode: Int,
    val bitmapWidth: Int,
    val bitmapHeight: Int,
    val imageViewWidth: Int,
    val imageViewHeight: Int,
    val issueType: IssueType
) : Serializable {
    var imageSrcBase64: String? = null
    var resourcePath: String? = null
    var resourceId: Int? = null
    var allocatedByteCount: Int? = null
    var byteCount: Int? = null
}

