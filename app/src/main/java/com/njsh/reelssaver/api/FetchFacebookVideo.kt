package com.njsh.reelssaver.api

import com.njsh.reelssaver.entity.EntityFBVideo

interface FetchFacebookVideo
{
    fun fetchVideo(callback: (CallResult<EntityFBVideo>) -> Unit)
}