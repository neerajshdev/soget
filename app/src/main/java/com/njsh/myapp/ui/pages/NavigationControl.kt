package com.njsh.myapp.ui.pages

import com.njsh.myapp.navigation.Page
import com.njsh.myapp.navigation.PageNavigator

val pageMap = mapOf(
    "Home" to Page(),
    "Youtube" to YoutubePage(),
    "Facebook" to Page(),
    "Instagram" to Page(),
    "My Downloads" to Page()
)

val Navigator: PageNavigator = PageNavigator(pageMap["Home"]!!)