package com.njsh.reelssaver.domain.models

data class VideoData(
    val result: String, // ok or err
    val video_url: String,
    val thumbnail_url: String
)
