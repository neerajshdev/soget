package com.njsh.reelssaver.shorts.data

import com.njsh.reelssaver.shorts.room.ShortVideo
import com.njsh.reelssaver.shorts.room.ShortVideoDatabase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*

interface ReadableDataSource<T> {
    suspend fun getData(page: Int, size: Int): List<T>
    suspend fun getAllData(): List<T>
    suspend fun count(): Long
}

interface ReadableNetworkSource<T> {
    suspend fun getData(size: Int): List<T>
}

interface WriteAbleDataSource<T> {
    suspend fun insert(data: T)
    suspend fun update(data: T)
    suspend fun deleteById(id: Long)
    suspend fun delete(data: T)
    suspend fun deleteAll()
}

/**
 * ShortVideo network source
 */
class NetworkSource : ReadableNetworkSource<ShortVideo> {
    private val uri = "http://192.168.0.102:80/status/random"

    override suspend fun getData(size: Int): List<ShortVideo> {
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
        }
        val response = httpClient.get("$uri?many=$size")
        val result = response.body<List<ShortVideo>>()
        httpClient.close()
        return result
    }
}

/**
 * ShortVideos local data source
 */
class LocalSource : ReadableDataSource<ShortVideo>, WriteAbleDataSource<ShortVideo> {
    private val database = ShortVideoDatabase.database()

    override suspend fun getData(page: Int, size: Int): List<ShortVideo> {
        val dao = database.shorVideoDao()
        val index = page * size
        return dao.get(index, size)
    }

    override suspend fun getAllData(): List<ShortVideo> {
        val dao = database.shorVideoDao()
        return dao.getAll()
    }

    override suspend fun insert(data: ShortVideo) {
        val dao = database.shorVideoDao()
        dao.insert(data)
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

object PagedShortVideos {
    private val localSource = LocalSource()
    private val networkSource = NetworkSource()

    var current = 0
    var pageSize = 10

    suspend fun getData(page: Int, size: Int): List<ShortVideo> {
        val index = page * size
        while (index + size >= localSource.count()) {
            networkSource.getData(size).forEach {
                localSource.insert(it)
            }
        }
        return localSource.getData(page, size)
    }

    suspend fun deleteAll() {
        localSource.deleteAll()
        current = 0
    }

    fun next() = current++

    fun prev() {
        current--
        if (current < 0) current = 0
    }

    suspend fun getPage(): List<ShortVideo> = getData(current, pageSize)
}

