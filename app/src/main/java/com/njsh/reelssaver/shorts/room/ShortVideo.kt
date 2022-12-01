package com.njsh.reelssaver.shorts.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ShortVideo")
data class ShortVideo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mpdUrl: String,
    val videoUrl: String,
    val likes: Long,
    val title: String,
    val thumbnailUrl: String
) {
    companion object {
        fun getDummy(): ShortVideo {
            return ShortVideo(
                videoUrl = "",
                mpdUrl = "https://cdn-cf.sharechat.com/contents/sc_3056680115/mpd/Jk1A9r7BmecRW9pp86lvHX2m.mpd",
                likes = 1000,
                title = "Drishyum2: Case Reopen",
                thumbnailUrl = ""
            )
        }
    }
}

