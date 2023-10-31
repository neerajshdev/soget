package com.njsh.reelssaver

import com.njsh.reelssaver.api.VideoDataFetch
import kotlinx.coroutines.runBlocking
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun videoData_fetch_test() {
        runBlocking {
            val baseUrl = "http://64.227.149.34:8000"
            val fetchVideoApi = VideoDataFetch(baseUrl)
            val result = fetchVideoApi.getVideoData("some-url")

            assert(result.isSuccess)

            if (result.isSuccess) {
                println(result.getOrNull())
            }
        }
    }

}