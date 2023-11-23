package com.gd.reelssaver.ads

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.gd.reelssaver.FirebaseKeys

object AdClickCounter {
    private var count = 0
    private val clicks = Firebase.remoteConfig.getLong(FirebaseKeys.CLICK_COUNT)

    fun check(): Boolean {
        val result = ++count >= clicks
        if (result) count = 0
        return result
    }
}