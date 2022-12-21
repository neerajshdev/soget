package com.njsh.reelssaver.shorts.data

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.FirebaseKeys
import com.njsh.reelssaver.layer.data.room.ShortVideoEntity
import com.njsh.reelssaver.layer.data.room.ShortVideoDatabase
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
class NetworkSource : ReadableNetworkSource<ShortVideoEntity> {
    private val uri = Firebase.remoteConfig.getString(FirebaseKeys.SHORT_API_URI)

    override suspend fun getData(size: Int): List<ShortVideoEntity> {
        val clien = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
        }
        val response = clien.get("$uri?many=$size")
        val result = response.body<List<ShortVideoEntity>>()
        clien.close()
        return result
    }
}

/**
 * ShortVideos local data source
 */
class LocalSource : ReadableDataSource<ShortVideoEntity>, WriteAbleDataSource<ShortVideoEntity> {
    private val database = ShortVideoDatabase.database()

    override suspend fun getData(offset: Int, limit: Int): List<ShortVideoEntity> {
        val dao = database.shorVideoDao()
        return dao.get(offset.toLong(), limit)
    }

    override suspend fun getAllData(): List<ShortVideoEntity> {
        val dao = database.shorVideoDao()
        return dao.getAll()
    }

    override suspend fun insertOrIgnore(data: ShortVideoEntity) {
        val dao = database.shorVideoDao()
        dao.insertOrIgnore(data)
    }

    override suspend fun update(data: ShortVideoEntity) {
        val dao = database.shorVideoDao()
        dao.update(data)
    }


    override suspend fun deleteAll() {
        database.shorVideoDao().deleteAll()
    }

    override suspend fun deleteById(id: Long) {
        database.shorVideoDao().delete(id)
    }

    override suspend fun delete(data: ShortVideoEntity) {
        database.shorVideoDao().delete(data.id)
    }

    override suspend fun count(): Long {
        return database.shorVideoDao().count()
    }
}

object ShortVideoRepo {
    private val localSource = LocalSource()
    private val networkSource = NetworkSource()

    suspend fun get(from: Int, limit: Int): List<ShortVideoEntity> = withContext(Dispatchers.IO) {
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

