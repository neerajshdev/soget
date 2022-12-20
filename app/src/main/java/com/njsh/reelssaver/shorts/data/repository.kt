package com.njsh.reelssaver.shorts.data

import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.App
import com.njsh.reelssaver.FirebaseKeys
import com.njsh.reelssaver.shorts.room.ShortVideo
import com.njsh.reelssaver.shorts.room.ShortVideoDatabase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "repository.kt"

interface ReadableDataSource<T> {
    suspend fun getData(page: Int, size: Int): List<T>
    suspend fun getAllData(): List<T>
    suspend fun count(): Long
}

interface ReadableNetworkSource<T> {
    suspend fun getData(size: Int): List<T>
}

interface WriteAbleDataSource<T> {
    suspend fun insertOrIgnore(data: T)
    suspend fun update(data: T)
    suspend fun deleteById(id: Long)
    suspend fun delete(data: T)
    suspend fun deleteAll()
}

/**
 * ShortVideo network source
 */
class NetworkSource : ReadableNetworkSource<ShortVideo> {
    private val uri = Firebase.remoteConfig.getString(FirebaseKeys.SHORT_API_URI)

    override suspend fun getData(size: Int): List<ShortVideo> {
        val clien = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
        }
        val response = clien.get("$uri?many=$size")
        val result = response.body<List<ShortVideo>>()
        clien.close()
        return result
    }
}

/**
 * ShortVideos local data source
 */
class LocalSource : ReadableDataSource<ShortVideo>, WriteAbleDataSource<ShortVideo> {
    private val database = ShortVideoDatabase.database()

    override suspend fun getData(offset: Int, limit: Int): List<ShortVideo> {
        val dao = database.shorVideoDao()
        return dao.get(offset, limit)
    }

    override suspend fun getAllData(): List<ShortVideo> {
        val dao = database.shorVideoDao()
        return dao.getAll()
    }

    override suspend fun insertOrIgnore(data: ShortVideo) {
        val dao = database.shorVideoDao()
        dao.insertOrIgnore(data)
    }

    override suspend fun update(data: ShortVideo) {
        val dao = database.shorVideoDao()
        dao.update(data)
    }


    override suspend fun deleteAll() {
        database.shorVideoDao().deleteAll()
    }

    override suspend fun deleteById(id: Long) {
        database.shorVideoDao().delete(id)
    }

    override suspend fun delete(data: ShortVideo) {
        database.shorVideoDao().delete(data.id)
    }

    override suspend fun count(): Long {
        return database.shorVideoDao().count()
    }
}

object ShortVideoRepo {
    private val localSource = LocalSource()
    private val networkSource = NetworkSource()

    suspend fun get(from: Int, limit: Int): List<ShortVideo> = withContext(Dispatchers.IO) {
        var count = localSource.count()
        while (from + limit > count - 1) {
            networkSource.getData(limit).forEach {
                localSource.insertOrIgnore(it)
            }
            count = localSource.count()
        }
        localSource.getData(from, limit)
    }

    suspend fun clear() {
        localSource.deleteAll()
    }
}

