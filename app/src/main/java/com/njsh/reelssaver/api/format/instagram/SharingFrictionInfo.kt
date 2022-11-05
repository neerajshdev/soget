package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class SharingFrictionInfo (

  @SerializedName("should_have_sharing_friction" ) var shouldHaveSharingFriction : Boolean? = null,
  @SerializedName("bloks_app_url"                ) var bloksAppUrl               : String?  = null

)