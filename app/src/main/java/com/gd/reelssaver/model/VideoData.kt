package com.gd.reelssaver.model

data class VideoData(
    val videoUrl: String,
    var imageUrl: String?,
    val dashManifest: String?
)