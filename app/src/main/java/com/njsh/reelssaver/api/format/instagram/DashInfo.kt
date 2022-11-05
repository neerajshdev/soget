package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class DashInfo (

  @SerializedName("is_dash_eligible"    ) var isDashEligible    : Boolean? = null,
  @SerializedName("video_dash_manifest" ) var videoDashManifest : String?  = null,
  @SerializedName("number_of_qualities" ) var numberOfQualities : Int?     = null

)