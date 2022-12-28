package com.njsh.reelssaver.layer.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ShortVideo")
data class ShortVideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mpdUrl: String,
    val videoUrl: String,
    val likes: Long,
    val title: String,
    val thumbnailUrl: String
)

