package com.centicbhaiya.getitsocial.api

import com.centicbhaiya.getitsocial.domain.models.VideoData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlin.coroutines.cancellation.CancellationException

class VideoDataFetch(
    private val baseUrl: String
) {
    class ExpectedStatusCodeOk(statusCode: HttpStatusCode) :
        RuntimeException("Expected a response status code to be OK: but got $statusCode")

    suspend fun getVideoData(url: String): Result<VideoData> {
        return try {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.get("$baseUrl/get-video-data") {
                contentType(ContentType.Application.Json)
                setBody(
                    """
                    {
                        "video_url": "$url"
                    }""".trimIndent()
                )
            }

            if (response.status != HttpStatusCode.OK) {
                throw ExpectedStatusCodeOk(response.status)
            }

            val res = response.body<VideoData>()
            Result.success(res)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Result.failure(ex)
        }
    }
}