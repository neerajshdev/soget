package com.njsh.instadl.api

import com.njsh.instadl.entity.EntityInstaReel

interface FetchInstaReel
{
    fun fetchReelData(callback: (CallResult<EntityInstaReel>) -> Unit)
}