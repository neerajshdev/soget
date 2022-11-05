package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName
import com.njsh.reelssaver.api.format.instagram.Graphql


data class ExampleJson2KtKotlin (

    @SerializedName("graphql"     ) var graphql     : Graphql? = Graphql(),
    @SerializedName("showQRModal" ) var showQRModal : Boolean? = null

)