package com.centicbhaiya.getitsocial

import com.centicbhaiya.getitsocial.api.VideoDataFetch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun VideoJsonParse_test() {
        val test_json = """
            {
    "key": "v=\"htpps://example.com\"",
  "html": "<div atrr=\" {\"key\": \"value\"} \" ></div>"
            }
        """.trimIndent()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = Gson().fromJson(test_json, type)
        println(map)
    }
}