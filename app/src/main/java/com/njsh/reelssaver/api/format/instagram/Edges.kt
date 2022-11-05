package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class Edges (

  @SerializedName("node" ) var node : Node? = Node()

)