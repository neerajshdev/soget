package com.njsh.reelssaver.shorts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.njsh.infinitelist.UniqueKey
import com.njsh.reelssaver.shorts.room.ShortVideo

sealed class DataModel(override val key: Int): UniqueKey {
    class ShortVideoModel(key: Int) : DataModel(key) {
        var shortVideo: ShortVideo? by mutableStateOf(null)
        var isSelected by mutableStateOf(false)
    }
}