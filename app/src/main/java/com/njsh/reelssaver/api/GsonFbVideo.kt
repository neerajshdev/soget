package com.njsh.reelssaver.api

import com.google.gson.annotations.SerializedName


data class GsonFbVideo (
    @SerializedName("video" ) var video : ArrayList<Video> = arrayListOf(),
    @SerializedName("tt"    ) var tt    : Boolean?         = null
)

data class Video (
    @SerializedName("video"     ) var video     : String? = null,
    @SerializedName("thumbnail" ) var thumbnail : String? = null
)
