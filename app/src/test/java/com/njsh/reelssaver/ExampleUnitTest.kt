package com.njsh.reelssaver

import com.njsh.reelssaver.api.CallResult
import com.njsh.reelssaver.api.FetchFacebookVideoImpl
import com.njsh.reelssaver.facebook.FacebookParser
import kotlinx.coroutines.runBlocking
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun fetch_facebook_video_test() {
        val fbVideoLink =
            "view-source:https://www.facebook.com/100044425394313/videos/131855143352262/?__so__=discover&__rv__=video_home_www_loe_popular_videos"
        val fetch = FetchFacebookVideoImpl(fbVideoLink)
        fetch.fetchVideo { result ->
            if (result is CallResult.Success) {
                println(result.data)
            }

            if (result is CallResult.Failed) {
                println(result.msg)
            }
        }
    }


    @Test
    fun download_using_ktor_test() = runBlocking {
        val url = "https://www.facebook.com/reel/638193618489688"

        val fbFacebookParser = FacebookParser()
        fbFacebookParser.findUrls(url).forEach {
            println(it)
        }
    }

}