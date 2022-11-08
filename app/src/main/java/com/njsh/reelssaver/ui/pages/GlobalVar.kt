package com.njsh.reelssaver.ui.pages

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.FirebaseKeys

const val appTitle = "All Video Downloader"
val doBackPressAds : Boolean get() =  Firebase.remoteConfig.getBoolean(FirebaseKeys.DO_BACKPRESS_ADS)