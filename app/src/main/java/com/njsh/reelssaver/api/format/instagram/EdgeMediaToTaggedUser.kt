package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class EdgeMediaToTaggedUser (

  @SerializedName("edges" ) var edges : ArrayList<String> = arrayListOf()

)