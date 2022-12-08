package com.njsh.reelssaver

import com.njsh.infinitelist.LinkedList
import com.njsh.reelssaver.api.CallResult
import com.njsh.reelssaver.api.FetchFacebookVideoImpl
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

    @Test
    fun nodeTest() {
        val list = LinkedList.with(listOf(0, 1, 2, 3, 4, 4, 6))

        println(list.format())
        println(list.formatReverse())

      /*  list.remove()
        list.removeFront()

        list.add(listOf(14, 78, 98, 0, 12, 13))
        list.addFront(listOf(14, 78))

        println(list.format())
        println(list.formatReverse())
        println("current: ${list.value}, size = ${list.size}")
        val head = list.head

        while (!head.isEnd()) {
            head.remove()
            println(head.format())
        }*/
    }
}