package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName
import com.njsh.reelssaver.api.format.instagram.Edges


data class EdgeMediaToCaption (

  @SerializedName("edges" ) var edges : ArrayList<Edges> = arrayListOf()

)