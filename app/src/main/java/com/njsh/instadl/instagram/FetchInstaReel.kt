package com.njsh.instadl.instagram

import android.util.Log
import com.google.gson.Gson
import com.njsh.instadl.Application
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

// val link = "https://www.instagram.com/reel/CjPctf_pMew/?__a=1&__d=dis"

class FetchInstaReel(private val url: String)
{
    var sessionId = "19755146960%3AC9Lcvq1YRCJH5r%3A24%3AAYfKKNZGZqmExlbFzM2dCiQjX_3PAwe9I7SgzvpIrQ"
    var ds_user_id = "19755146960"
    var eHandleInvalidInput = {}

    operator fun invoke(): EntityInstaReel?
    {
        val link = verifyUrl(url)
        if (link != null)
        {
            return fetch(link)
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
            eHandleInvalidInput()
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
            "Cookie",
            "sessionid=$sessionId; ds_user_id=$ds_user_id"
        ).build()

        val call = client.newCall(request);
        val response = call.execute()
        val jsonContent = response.body?.string()
        val gson = Gson()
        try
        {
            val data = gson.fromJson(jsonContent, InstaReelData::class.java)
            val uri = URI(data.items[0].video_versions[0].url)
            val ext = uri.path.substring(uri.path.lastIndexOf(".") + 1)

            if (Application.debug)
            Log.d(Application.TAG, "ext = $ext")

            return EntityInstaReel(
                title = data.items[0].caption.text,
                imageUrl = data.items[0].image_versions2.candidates[0].url,
                width = data.items[0].image_versions2.candidates[0].width,
                height = data.items[0].image_versions2.candidates[0].height,
                ext = ext,
                duration = data.items[0].video_duration,
                url = data.items[0].video_versions[0].url
            )
        } catch (ex: Exception)
        {
            ex.printStackTrace()
            return null
        }
    }
}