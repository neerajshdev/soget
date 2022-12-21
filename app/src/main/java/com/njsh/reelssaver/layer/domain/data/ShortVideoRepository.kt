package com.njsh.reelssaver.layer.domain.data

import com.njsh.reelssaver.layer.domain.models.ShortVideoModel

interface ShortVideoRepository {
    suspend fun insert(short: ShortVideoModel)
    suspend fun get(offset: Long, limit: Int): List<ShortVideoModel>
    suspend fun clear()
    suspend fun delete(id: Long)
}