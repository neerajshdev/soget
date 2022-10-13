package com.njsh.instadl.ui.pages

import com.njsh.instadl.navigation.Page
import com.njsh.instadl.navigation.PageNavigator

val pageMap = mapOf(
    "Home" to Page(),
    "Youtube" to PageYoutube(),
    "Facebook" to Page(),
    "Instagram" to PageInstagram(),
    "My Downloads" to PageDownloads()
)

val Navigator: PageNavigator = PageNavigator(pageMap["Home"]!!)