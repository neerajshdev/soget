package com.njsh.instadl.usecase

import android.util.Log
import com.google.gson.Gson
import com.njsh.instadl.App
import com.njsh.instadl.entity.EntityInstaReel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

// val link = "https://www.instagram.com/reel/CjPctf_pMew/?__a=1&__d=dis"

class FetchInstaReel(private val url: String)
{
    lateinit var sessionId: String
    lateinit var ds_user_id: String
    var handleExcep: (Exception) -> Unit = {}

    operator fun invoke(): EntityInstaReel?
    {
        try
        {
            val link = verifyUrl(url)
            if (link != null)
            {
                return fetch(link)
            }
        } catch (ex: Exception)
        {
            handleExcep(ex)
        }
        return null
    }


    private fun verifyUrl(url: String): String?
    {
        var result: String? = null
        val uri = URI(url)
        if (uri.host == "www.instagram.com")
        {
            result = "${uri.scheme}://${uri.authority}/${uri.path}?__a=1&__d=dis"
        } else
        {
            throw IllegalArgumentException("Invalid url")
        }
        return result
    }

    private fun fetch(link: String): EntityInstaReel?
    {
        val client = OkHttpClient()
        val request = Request.Builder().url(link).header(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37"
        ).header(
            "Cookie", "sessionid=$sessionId; ds_user_id=$ds_user_id"
        ).build()

        val call = client.newCall(request);
        val response = call.execute()
        val jsonContent = response.body?.string()
        val gson = Gson()
        val data = gson.fromJson(jsonContent, InstaReelData::class.java)
        val uri = URI(data.items[0].video_versions[0].url)
        val ext = uri.path.substring(uri.path.lastIndexOf(".") + 1)

        if (App.debug) Log.d(App.TAG, "ext = $ext")

        return EntityInstaReel(
            title = data.items[0].caption?.text ?: "InstagramReel",
            imageUrl = data.items[0].image_versions2.candidates[0].url,
            width = data.items[0].image_versions2.candidates[0].width,
            height = data.items[0].image_versions2.candidates[0].height,
            ext = ext,
            duration = data.items[0].video_duration,
            url = data.items[0].video_versions[0].url
        )
    }
}