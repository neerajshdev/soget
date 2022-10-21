package com.njsh.instadl.api

import com.google.gson.Gson
import com.njsh.instadl.entity.EntityFBVideo
import com.njsh.instadl.usecase.GsonFbVideo
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

class FetchFacebookVideoImpl(private val videoUrl: String) : FetchFacebookVideo
{
    private val apiUrl = "https://api.instavideosave.com/fb"

    override fun fetchVideo(callback: (CallResult<EntityFBVideo>) -> Unit)
    {
        try
        {
            val uri = URI(videoUrl)
            if (!verify(uri))
            {
                callback(CallResult.Failed("invalid url: $videoUrl"))
            }
            val client = OkHttpClient()
            val request = Request.Builder().url(apiUrl).header("url", videoUrl).header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36"
            ).build()

            val response = client.newCall(request).execute()
            val jsonData: String? = response.body?.string()

            val data = Gson().fromJson(jsonData, GsonFbVideo::class.java)
            val url = data.video[0].video
            val thumbnail = data.video[0].thumbnail
            val ext = with(URI(url)) {
                path.substring(path.lastIndexOf(".") + 1)
            }
            val fbVideo = EntityFBVideo(url!!, "Video/$ext", thumbnail!!)
            callback(CallResult.Success(fbVideo))
        } catch (ex: Exception)
        {
            callback(CallResult.Failed(ex.message ?: "Something went wrong"))
        }
    }

    private fun verify(url: URI): Boolean
    {
        if (url.host == "fb.watch" || url.host == "www.facebook.com")
        {
            return true
        }
        return false
    }
}