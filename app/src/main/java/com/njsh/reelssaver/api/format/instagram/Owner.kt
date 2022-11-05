package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class Owner (

    @SerializedName("id"                           ) var id                        : String?                   = null,
    @SerializedName("is_verified"                  ) var isVerified                : Boolean?                  = null,
    @SerializedName("profile_pic_url"              ) var profilePicUrl             : String?                   = null,
    @SerializedName("username"                     ) var username                  : String?                   = null,
    @SerializedName("blocked_by_viewer"            ) var blockedByViewer           : Boolean?                  = null,
    @SerializedName("restricted_by_viewer"         ) var restrictedByViewer        : String?                   = null,
    @SerializedName("followed_by_viewer"           ) var followedByViewer          : Boolean?                  = null,
    @SerializedName("full_name"                    ) var fullName                  : String?                   = null,
    @SerializedName("has_blocked_viewer"           ) var hasBlockedViewer          : Boolean?                  = null,
    @SerializedName("is_embeds_disabled"           ) var isEmbedsDisabled          : Boolean?                  = null,
    @SerializedName("is_private"                   ) var isPrivate                 : Boolean?                  = null,
    @SerializedName("is_unpublished"               ) var isUnpublished             : Boolean?                  = null,
    @SerializedName("requested_by_viewer"          ) var requestedByViewer         : Boolean?                  = null,
    @SerializedName("pass_tiering_recommendation"  ) var passTieringRecommendation : Boolean?                  = null,
    @SerializedName("edge_owner_to_timeline_media" ) var edgeOwnerToTimelineMedia  : EdgeOwnerToTimelineMedia? = EdgeOwnerToTimelineMedia(),
    @SerializedName("edge_followed_by"             ) var edgeFollowedBy            : EdgeFollowedBy?           = EdgeFollowedBy()

)