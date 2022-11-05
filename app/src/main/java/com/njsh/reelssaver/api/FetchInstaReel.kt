package com.njsh.reelssaver.api

import com.njsh.reelssaver.entity.EntityInstaReel

interface FetchInstaReel
{
    fun fetchReelData(callback: (CallResult<EntityInstaReel>) -> Unit)
}