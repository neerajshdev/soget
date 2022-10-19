package com.njsh.instadl

import com.njsh.instadl.usecase.FetchInstaReel
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
        val link = "https://www.instagram.com/reel/CjPctf_pMew/?__a=1&__d=dis"
        val result = FetchInstaReel(link).invoke()
    }
}