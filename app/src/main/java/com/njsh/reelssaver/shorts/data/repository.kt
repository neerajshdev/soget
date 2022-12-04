package com.njsh.reelssaver.shorts.data

import android.util.Log
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

    override suspend fun getData(offset: Int, limit: Int): List<ShortVideo> {
        val dao = database.shorVideoDao()
        return dao.get(offset, limit)
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

class Part<T: Any> (val startIndex: Int, val array: Array<T>) {
    val lastIndex: Int get() = array.lastIndex + startIndex
    val size: Int get() = array.size + startIndex

    fun addToEnd(other: Part<T>): Part<T> {
        val newArray = array + other.array
        return Part(startIndex, newArray)
    }

    fun addToStart(other: Part<T>): Part<T> {
        val newArray = array + other.array
        return Part(startIndex - other.array.size, newArray)
    }

    fun dropFromStart(many: Int):Part<T> {
        val newArray = array.copyOfRange(0 + many, array.lastIndex)
        return Part(startIndex + many, newArray)
    }


    fun dropFromEnd(many: Int): Part<T> {
        val newArray = array.copyOfRange(0, array.lastIndex - many)
        return Part(startIndex, newArray)
    }

    operator fun get(index: Int): T {
        val actualIndex = index - startIndex
        return array[actualIndex]
    }

    fun isEmpty() = array.isEmpty()
    fun isNotEmpty() = array.isNotEmpty()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        array.forEach {
            stringBuilder.append(it)
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}

object Repository {
    private val localSource = LocalSource()
    private val networkSource = NetworkSource()

    suspend fun get(from: Int, limit: Int): List<ShortVideo> = withContext(Dispatchers.IO) {
        var count = localSource.count()
        Log.d(TAG, "get(form = $from, limit = $limit) count = $count")
        while (from + limit > count - 1) {
            networkSource.getData(limit).forEach {
                localSource.insert(it)
                Log.d(TAG, "inserting $it to localSource")
            }
            count = localSource.count()
        }
        Log.d(TAG, "get: count = $count, from = $from, limit = $limit")
        localSource.getData(from, limit)
    }
}

