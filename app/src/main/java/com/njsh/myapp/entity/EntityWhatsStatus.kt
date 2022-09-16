package com.njsh.myapp.entity

class EntityWhatsStatus(
    val file :String,
    val isContentUri: Boolean,
    val type: Type,
)
{
    enum class Type
    {
        VIDEO,
        IMAGE
    }
}