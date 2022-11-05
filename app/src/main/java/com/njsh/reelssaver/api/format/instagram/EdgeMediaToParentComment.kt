package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName
import com.njsh.reelssaver.api.format.instagram.Edges
import com.njsh.reelssaver.api.format.instagram.PageInfo


data class EdgeMediaToParentComment (

  @SerializedName("count"     ) var count    : Int?             = null,
  @SerializedName("page_info" ) var pageInfo : PageInfo?        = PageInfo(),
  @SerializedName("edges"     ) var edges    : ArrayList<Edges> = arrayListOf()

)