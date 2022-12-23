package com.njsh.reelssaver.layer.domain.use_cases

import android.util.Log
import com.google.gson.GsonBuilder
import com.njsh.reelssaver.App
import com.njsh.reelssaver.api.GsonReelData
import com.njsh.reelssaver.api.format.instagram.GsonGraphQl
import com.njsh.reelssaver.layer.domain.models.ReelModel
import okhttp3.*
import java.io.IOException
import java.net.URI

class FetchReelUseCase(
    private val url: String,
    private val dsUserId: String,
    private val sessionId: String
) {
    operator fun invoke(result: (Result<ReelModel>)-> Unit) {
        try {
            val checkedUrl = verifyUrl(url)
            fetch(checkedUrl, result)
        } catch (ex: Exception) {
            result(Result.failure(RuntimeException(ex)))
        }
    }


    private fun verifyUrl(url: String): String {
        val result: String?
        val uri = URI(url)
        if (uri.host == "www.instagram.com") {
            result = "${uri.scheme}://${uri.authority}/${uri.path}?__a=1&__d=dis"
        } else {
            throw IllegalArgumentException("Invalid url")
        }
        return result
    }

    private fun fetch(
        link: String, result: (Result<ReelModel>)-> Unit
    ) {
        val client = OkHttpClient()
        val request = Request.Builder().url(link).header(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37"
        ).header(
            "Cookie", "sessionid=$sessionId; ds_user_id=${this.dsUserId}"
        ).build()

        val call = client.newCall(request)
        call.enqueue(responseCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val gson = GsonBuilder().setLenient().create()

                if (json != null && json.contains("graphql")) {
                    try {
                        val dto = gson.fromJson(json, GsonGraphQl::class.java)
                        val reelModel = fromGraphQl(dto)
                        result(Result.success(reelModel))
                    } catch (ex: Exception) {
                        result(Result.failure(ex))
                    }
                } else {
                    try {
                        val dto = gson.fromJson(json, GsonReelData::class.java)
                        val reelModel =  fromGsonReelData(dto)
                        result(Result.success(reelModel))
                    } catch (ex: Exception) {
                        result(Result.failure(ex))
                    }
                }
            }
        })
    }

    private fun fromGsonReelData(data: GsonReelData): ReelModel {
        val uri = URI(data.items[0].video_versions[0].url)
        val ext = uri.path.substring(uri.path.lastIndexOf(".") + 1)

        if (App.debug) Log.d(App.TAG, "ext = $ext")

        return ReelModel(
            title = data.items[0].caption?.text ?: "InstagramReel",
            imageUrl = data.items[0].image_versions2.candidates[0].url,
            width = data.items[0].image_versions2.candidates[0].width,
            height = data.items[0].image_versions2.candidates[0].height,
            type = "Video/$ext",
            duration = data.items[0].video_duration,
            url = data.items[0].video_versions[0].url
        )
    }

    private fun fromGraphQl(gsonGraphQl: GsonGraphQl): ReelModel {
        val graphql = gsonGraphQl.graphql!!
        val title = graphql.shortcodeMedia?.title ?: ""
        val width = graphql.shortcodeMedia?.dimensions?.width!!
        val height = graphql.shortcodeMedia?.dimensions?.height!!
        val duration = graphql.shortcodeMedia?.videoDuration!!
        val imageUrl = graphql.shortcodeMedia?.thumbnailSrc!!
        val url = graphql.shortcodeMedia?.videoUrl!!

        val uri = URI(url)
        val ext = uri.path.substring(uri.path.lastIndexOf(".") + 1)

        return ReelModel(
            title, imageUrl, url, "Video/$ext", width, height, duration.toFloat()
        )
    }
}