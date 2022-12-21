package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class GsonGraphQl (

    @SerializedName("graphql"     ) var graphql     : Graphql? = Graphql(),
    @SerializedName("showQRModal" ) var showQRModal : Boolean? = null

)