package com.njsh.reelssaver.layer.data

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.FirebaseKeys
import com.njsh.reelssaver.layer.data.room.ShortVideoDatabase
import com.njsh.reelssaver.layer.data.room.ShortVideoEntity
import com.njsh.reelssaver.layer.domain.data.ShortVideoRepository
import com.njsh.reelssaver.layer.domain.models.ShortVideoModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*

class ShortVideoRepositoryImpl: ShortVideoRepository {
    private val shortVideoDao = ShortVideoDatabase.database().shorVideoDao()
    private val apiUrl = Firebase.remoteConfig.getString(FirebaseKeys.SHORT_API_URI)

    override suspend fun insert(short: ShortVideoModel) {
        shortVideoDao.insertOrIgnore(short.toRoomEntity())
    }

    override suspend fun get(offset: Long, limit: Int): List<ShortVideoModel> {
        var lastRowIndex = shortVideoDao.count() - 1
        val lastIx = offset + limit - 1
        while (lastIx > lastRowIndex) {
            val result = requestShortVideos((lastIx - lastRowIndex).toInt())
            result.forEach { shortVideoDao.insertOrIgnore(it.toRoomEntity()) }
            lastRowIndex = shortVideoDao.count() - 1
        }
        return shortVideoDao.get(offset, limit).map { it.toDomainModel() }
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }


    private fun ShortVideoModel.toRoomEntity(): ShortVideoEntity {
        return ShortVideoEntity(id, mpdUrl, videoUrl, likes, title, thumbnailUrl)
    }

    private fun ShortVideoEntity.toDomainModel(): ShortVideoModel {
        return ShortVideoModel(id, mpdUrl, videoUrl, likes, title, thumbnailUrl)
    }

    private suspend fun requestShortVideos(many: Int): List<ShortVideoModel>{
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
        }
        val response = client.get("$apiUrl?many=$many")
        val result = response.body<List<ShortVideoModel>>()
        client.close()
        return result
    }
}