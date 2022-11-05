package com.njsh.reelssaver.api.format.instagram

import com.google.gson.annotations.SerializedName


data class ClipsMusicAttributionInfo (

  @SerializedName("artist_name"              ) var artistName            : String?  = null,
  @SerializedName("song_name"                ) var songName              : String?  = null,
  @SerializedName("uses_original_audio"      ) var usesOriginalAudio     : Boolean? = null,
  @SerializedName("should_mute_audio"        ) var shouldMuteAudio       : Boolean? = null,
  @SerializedName("should_mute_audio_reason" ) var shouldMuteAudioReason : String?  = null,
  @SerializedName("audio_id"                 ) var audioId               : String?  = null

)