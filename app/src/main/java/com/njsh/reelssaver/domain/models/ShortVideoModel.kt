package com.njsh.reelssaver.domain.models

data class ShortVideoModel(
    val id: Long = 0,
    val mpdUrl: String,
    val videoUrl: String,
    val likes: Long,
    val title: String,
    val thumbnailUrl: String
) {
    companion object {
        fun createFakeModel() = ShortVideoModel(
            mpdUrl = "", videoUrl = "", likes = 8888, title = "super hit status", thumbnailUrl = ""
        )
    }
}


