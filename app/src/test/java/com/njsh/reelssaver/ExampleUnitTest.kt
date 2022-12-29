package com.njsh.reelssaver

import com.njsh.reelssaver.api.CallResult
import com.njsh.reelssaver.api.FetchFacebookVideoImpl
import com.njsh.reelssaver.layer.domain.use_cases.FetchFBVideoUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun sample() {
        val fbVideoLink = "https://fb.watch/gesoBtUCWa/"
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
    fun facebookFetchUsecase() {

        val videoUrl = "https://www.facebook.com/100014705387870/videos/5876328675761327/"

        runBlocking {
            val deferred = CompletableDeferred<Unit>()
            FetchFBVideoUseCase(videoUrl).invoke(
                onSuccess = {result->
                    println("fetch succeed with result: $result")
                    deferred.complete(Unit)
                },
                onFailure = {
                    it.printStackTrace()
                    deferred.complete(Unit)
                }
            )
            deferred.await()
        }
    }
}