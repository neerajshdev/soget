package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class EdgeThreadedComments (

  @SerializedName("count"     ) var count    : Int?             = null,
  @SerializedName("page_info" ) var pageInfo : PageInfo?        = PageInfo(),
  @SerializedName("edges"     ) var edges    : ArrayList<Edges> = arrayListOf()

)