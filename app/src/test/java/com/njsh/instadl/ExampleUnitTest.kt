package com.njsh.instadl

import com.njsh.instadl.usecase.FetchFacebookVideoImpl
import com.njsh.instadl.api.CallResult
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest
{
    @Test
    fun sample()
    {
        val fbVideoLink = "https://fb.watch/gesoBtUCWa/"
        val fetch = FetchFacebookVideoImpl(fbVideoLink)
        fetch.fetchVideo { result ->
            if (result is CallResult.Success)
            {
                println(result.data)
            }

            if (result is CallResult.Failed)
            {
                println(result.msg)
            }
        }
    }
}