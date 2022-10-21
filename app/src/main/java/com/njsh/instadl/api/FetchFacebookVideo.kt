package com.njsh.instadl.api

import com.njsh.instadl.entity.EntityFBVideo

interface FetchFacebookVideo
{
    fun fetchVideo(callback: (CallResult<EntityFBVideo>) -> Unit)
}