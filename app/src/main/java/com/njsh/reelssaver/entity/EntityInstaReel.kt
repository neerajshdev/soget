package com.njsh.reelssaver.entity

import com.njsh.reelssaver.api.Downloadable
import com.njsh.reelssaver.api.DownloadableImpl

class EntityInstaReel(
    title: String,
    val imageUrl: String,
    url: String,
    type: String,
    val width: Int,
    val height: Int,
    val duration: Float
) : Downloadable by DownloadableImpl(url, title, type)