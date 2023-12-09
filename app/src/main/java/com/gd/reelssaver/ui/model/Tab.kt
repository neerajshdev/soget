package com.gd.reelssaver.ui.model

import java.util.UUID

data class Tab(
    val url: String,
    val id: String = UUID.randomUUID().toString()
)