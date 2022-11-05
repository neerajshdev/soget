package com.njsh.reelssaver.api

import android.util.Log
import com.google.gson.GsonBuilder
import com.njsh.reelssaver.App
import com.njsh.reelssaver.api.format.instagram.ExampleJson2KtKotlin
import com.njsh.reelssaver.entity.EntityInstaReel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

class FetchInstaReelImpl(
    private val url: String, val dsUserId: String, val sessionId: String
) : FetchInstaReel {
    override fun fetchReelData(callback: (CallResult<EntityInstaReel>) -> Unit) {
        try {
            val checkedUrl = verifyUrl(url)
            if (checkedUrl != null) {
                val data = fetch(checkedUrl)!!
                val result = CallResult.Success(data)
                callback(result)
            }
        } catch (ex: Exception) {
            callback(CallResult.Failed(ex.message ?: "Something went wrong!"))
        }
    }


    private fun verifyUrl(url: String): String? {
        var result: String?
        val uri = URI(url)
        if (uri.host == "www.instagram.com") {
            result = "${uri.scheme}://${uri.authority}/${uri.path}?__a=1&__d=dis"
        } else {
            throw IllegalArgumentException("Invalid url")
        }
        return result
    }

    private fun fetch(link: String): EntityInstaReel? {
        val client = OkHttpClient()
        val request = Request.Builder().url(link).header(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37"
        ).header(
            "Cookie", "sessionid=$sessionId; ds_user_id=${this.dsUserId}"
        ).build()

        val call = client.newCall(request)
        val response = call.execute()
        val jsonContent = response.body?.string()

        val gson = GsonBuilder().setLenient().create()
        try {
            val data = gson.fromJson(jsonContent, GsonReelData::class.java)
            return fromGsonReelData(data)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        try {
            val data = gson.fromJson(jsonContent, ExampleJson2KtKotlin::class.java)
            return fromGraphQl(data)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    private fun fromGsonReelData(data: GsonReelData): EntityInstaReel {
        val uri = URI(data.items[0].video_versions[0].url)
        val ext = uri.path.substring(uri.path.lastIndexOf(".") + 1)

        if (App.debug) Log.d(App.TAG, "ext = $ext")

        return EntityInstaReel(
            title = data.items[0].caption?.text ?: "InstagramReel",
            imageUrl = data.items[0].image_versions2.candidates[0].url,
            width = data.items[0].image_versions2.candidates[0].width,
            height = data.items[0].image_versions2.candidates[0].height,
            type = "Video/$ext",
            duration = data.items[0].video_duration,
            url = data.items[0].video_versions[0].url
        )
    }

    private fun fromGraphQl(gsonGraphQl: ExampleJson2KtKotlin): EntityInstaReel {
        val graphql = gsonGraphQl.graphql!!
        val title = graphql.shortcodeMedia?.title!!
        val width = graphql.shortcodeMedia?.dimensions?.width!!
        val height = graphql.shortcodeMedia?.dimensions?.height!!
        val duration = graphql.shortcodeMedia?.videoDuration!!
        val imageUrl = graphql.shortcodeMedia?.thumbnailSrc!!
        val url = graphql.shortcodeMedia?.videoUrl!!

        val uri = URI(url)
        val ext = uri.path.substring(uri.path.lastIndexOf(".") + 1)

        return EntityInstaReel(
            title, imageUrl, url, "Video/$ext", width, height, duration.toFloat()
        )
    }
}