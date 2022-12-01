package com.njsh.reelssaver.shorts.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.njsh.reelssaver.App

@Database(entities = [ShortVideo::class], version = 1)
abstract class ShortVideoDatabase : RoomDatabase() {
    abstract fun shorVideoDao(): ShortVideoDao

    companion object {
        fun database(): ShortVideoDatabase {
            return lazy {
                Room.databaseBuilder(
                    App.instance(),
                    ShortVideoDatabase::class.java,
                    "LocalShortVideos"
                ).build()
            }.value
        }
    }
}