package com.desidev.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val testUrl =
        "https://scontent.fdel24-1.fna.fbcdn.net/v/t42.1790-2/409326485_314328708142241_242782183691640083_n.mp4?_nc_cat=110&ccb=1-7&_nc_sid=55d0d3&efg=eyJybHIiOjc3NywicmxhIjo4MzcsInZlbmNvZGVfdGFnIjoic3ZlX3NkIn0%3D&_nc_ohc=evseQJsxy20AX-ciKuL&_nc_rml=0&_nc_ht=scontent.fdel24-1.fna&oh=00_AfC4MPGodNlboNAGFCt-WPHAdIkwq5-ajriNfVTD5uT0qw&oe=6591C011"


    @Test
    fun flowChannel() {
        val flow = channelFlow {
            launch (Dispatchers.IO){
                repeat(100) {
                     send(it)
                }
            }
        }

        runBlocking {
            flow.collect {
                println("collecting: $it")
            }
        }
    }

    @Test
    fun downloadTest() {
        val dir = ExampleUnitTest::class.java.getResource("").path.let { File(it) }
        val downloader: Downloader = Downloader(dir)

        runBlocking {
            val flow = downloader.addDownload(testUrl, dir)

            flow.collect { event: DownloadEvent ->
                when (event) {
                    is DownloadEvent.OnProgress -> {
                        println("On download update: ${event.download}")
                    }

                    is DownloadEvent.OnCancelled -> {
                        println("on Download Cancelled: ${event.download}")
                    }

                    is DownloadEvent.OnAddNew -> {
                        println("on add new Download: ${event.download}")
                    }
                    is DownloadEvent.OnComplete -> {
                        println("on Download Complete: ${event.download}")
                    }
                }
            }
        }
    }
}