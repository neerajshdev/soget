package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class DisplayResources (

  @SerializedName("src"           ) var src          : String? = null,
  @SerializedName("config_width"  ) var configWidth  : Int?    = null,
  @SerializedName("config_height" ) var configHeight : Int?    = null

)