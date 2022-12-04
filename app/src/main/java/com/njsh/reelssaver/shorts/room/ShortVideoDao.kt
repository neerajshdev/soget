package com.njsh.reelssaver.shorts.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShortVideoDao {
    @Insert
    suspend fun insert(data: ShortVideo)

    @Update
    suspend fun update(data: ShortVideo)

    @Query("delete from ShortVideo where id = :id")
    suspend fun delete(id: Long)

    @Query("delete from ShortVideo")
    suspend fun deleteAll()

    @Query("SELECT * FROM ShortVideo")
    suspend fun getAll(): List<ShortVideo>


    @Query("SELECT * FROM ShortVideo LIMIT :limit OFFSET :offset;" )
    suspend fun get(offset: Int, limit: Int): List<ShortVideo>

    @Query("SELECT COUNT(id) FROM ShortVideo")
    suspend fun count(): Long
}