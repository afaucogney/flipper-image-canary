package fr.afaucogney.mobile.flipper.internal

import java.io.Serializable

enum class IssueType : Serializable {
    BITMAP_QUALITY_TOO_HIGH, BITMAP_QUALITY_TOO_LOW, INVISIBLE_BUT_MEMORY_OCCUPIED, NONE
}
