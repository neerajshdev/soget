package com.njsh.instadl.entity

import com.njsh.instadl.api.Downloadable
import com.njsh.instadl.api.DownloadableImpl

class EntityFBVideo(
    videoUrl: String,
    type: String,
    val thumbnail: String
) : Downloadable by DownloadableImpl(videoUrl, "facebook video", type)