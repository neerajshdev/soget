package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class Node (

  @SerializedName("id"                    ) var id                  : String?      = null,
  @SerializedName("text"                  ) var text                : String?      = null,
  @SerializedName("created_at"            ) var createdAt           : Int?         = null,
  @SerializedName("did_report_as_spam"    ) var didReportAsSpam     : Boolean?     = null,
  @SerializedName("owner"                 ) var owner               : Owner?       = Owner(),
  @SerializedName("viewer_has_liked"      ) var viewerHasLiked      : Boolean?     = null,
  @SerializedName("edge_liked_by"         ) var edgeLikedBy         : EdgeLikedBy? = EdgeLikedBy(),
  @SerializedName("is_restricted_pending" ) var isRestrictedPending : Boolean?     = null

)