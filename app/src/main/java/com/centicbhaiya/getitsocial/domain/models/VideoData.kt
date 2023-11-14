package com.centicbhaiya.getitsocial.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoData(
    val result: String, // ok or err
    val video_url: String,
    val thumbnail_url: String
)
