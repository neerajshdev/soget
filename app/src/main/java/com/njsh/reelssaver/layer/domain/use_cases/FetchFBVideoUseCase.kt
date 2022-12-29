package com.njsh.reelssaver.layer.domain.use_cases

import com.google.gson.Gson
import com.njsh.reelssaver.api.GsonFbVideo
import com.njsh.reelssaver.layer.domain.models.FbVideoModel
import okhttp3.*
import java.io.IOException
import java.net.URI

class FetchFBVideoUseCase(
    private val url: String
) {
    private val apiUrl = "https://fb.instavideosave.com/"

    operator fun invoke(
        onSuccess: (FbVideoModel) -> Unit, onFailure: (Exception) -> Unit
    ) {
        try {
            val uri = URI(url)
            verify(uri)
            val client = OkHttpClient()
            val request = Request.Builder().url(apiUrl).header("url", url).header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36"
            ).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonData: String? = response.body?.string()
                        val data = Gson().fromJson(jsonData, GsonFbVideo::class.java)
                        val url = data.video[0].video
                        val thumbnail = data.video[0].thumbnail
                        val ext = with(URI(url)) {
                            path.substring(path.lastIndexOf(".") + 1)
                        }
                        val fbVideo = FbVideoModel(url!!, "Video/$ext", thumbnail!!)
                        onSuccess(fbVideo)
                    } catch (ex: Exception) {
                        onFailure(ex)
                    }
                }
            })
        } catch (ex: Exception) {
            onFailure(ex)
        }
    }

    private fun verify(url: URI) {
        if (!(url.host == "fb.watch" || url.host == "www.facebook.com")) {
            throw IllegalArgumentException("uri is not a valid facebook video link")
        }
    }
}