package com.njsh.reelssaver.entity

import com.njsh.reelssaver.api.Downloadable
import com.njsh.reelssaver.api.DownloadableImpl

class EntityFBVideo(
    videoUrl: String,
    type: String,
    val thumbnail: String
) : Downloadable by DownloadableImpl(videoUrl, "facebook video", type)