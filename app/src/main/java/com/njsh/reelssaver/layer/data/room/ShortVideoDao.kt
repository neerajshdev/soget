package com.njsh.reelssaver.layer.data.room

import androidx.room.*

@Dao
interface ShortVideoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(data: ShortVideoEntity)

    @Update
    suspend fun update(data: ShortVideoEntity)

    @Query("delete from ShortVideo where id = :id")
    suspend fun delete(id: Long)

    @Query("delete from ShortVideo")
    suspend fun deleteAll()

    @Query("SELECT * FROM ShortVideo")
    suspend fun getAll(): List<ShortVideoEntity>


    @Query("SELECT * FROM ShortVideo LIMIT :limit OFFSET :offset;" )
    suspend fun get(offset: Long, limit: Int): List<ShortVideoEntity>

    @Query("SELECT COUNT(id) FROM ShortVideo")
    suspend fun count(): Long
}