package com.gd.reelssaver.model

class EntityWhatsStatus(
    val file :String,
    val isContentUri: Boolean,
    val type: Type,
) {
    enum class Type
    {
        VIDEO,
        IMAGE
    }
}