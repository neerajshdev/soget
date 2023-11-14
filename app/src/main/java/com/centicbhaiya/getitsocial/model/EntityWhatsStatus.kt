package com.centicbhaiya.getitsocial.model

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