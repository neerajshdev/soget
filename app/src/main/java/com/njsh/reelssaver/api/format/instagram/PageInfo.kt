package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class PageInfo (

  @SerializedName("has_next_page" ) var hasNextPage : Boolean? = null,
  @SerializedName("end_cursor"    ) var endCursor   : String?  = null

)