package com.njsh.instadl.entity

import com.njsh.instadl.api.Downloadable
import com.njsh.instadl.api.DownloadableImpl

class EntityInstaReel(
    title: String,
    val imageUrl: String,
    url: String,
    type: String,
    val width: Int,
    val height: Int,
    val duration: Float
) : Downloadable by DownloadableImpl(url, title, type)