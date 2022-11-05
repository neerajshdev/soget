package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class EdgeMediaPreviewLike (

  @SerializedName("count" ) var count : Int?              = null,
  @SerializedName("edges" ) var edges : ArrayList<String> = arrayListOf()

)