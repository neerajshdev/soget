package com.gd.reelssaver.ui.router

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Destination<C: Parcelable>(
    val config: C,
    val type: Direction
): Parcelable {
    enum class Direction { FORWARD, BACKWARD }
}
