package com.njsh.reelssaver.entity

data class ShortVideo (
    var videoUrl: String,
    var mpdUrl: String,
    var likes: String,
    var label: String,
) {
    companion object {
        fun getDummy(): ShortVideo {
            return ShortVideo(
                videoUrl = "",
                mpdUrl = "https://dash.akamaized.net/dash264/TestCasesIOP33/adapatationSetSwitching/5/manifest.mpd",
                likes = "2.4k",
                label = "Drishyum2: Case Reopen"
            )
        }
    }
}