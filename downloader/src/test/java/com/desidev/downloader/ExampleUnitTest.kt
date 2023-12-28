package com.desidev.downloader

import kotlinx.coroutines.runBlocking
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
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun downloadTest() {
        val dir = ExampleUnitTest::class.java.getResource("").path.let { File(it) }
        val downloader: Downloader = Downloader(dir)

        runBlocking {
            val flow = when (val result = downloader.addDownload(testUrl, dir)) {
                is Result.Ok -> result.value
                is Result.Err -> {
                    when (val err = result.err) {
                        is Error.ServerDisAllowed -> {
                            println("Failed with status code! : ${err.statusCode}")
                        }

                        is Error.FailedWithIoException -> {
                            println("Failed with io exception : ${err.ex}")
                        }
                    }
                    return@runBlocking
                }
            }

            flow.collect { event: DownloadEvent ->
                when (event) {
                    is DownloadEvent.OnProgress -> {
                        println("On download update: ${event.download}")
                    }

                    is DownloadEvent.OnCancelled -> {
                    }

                    is DownloadEvent.OnAddNew -> {}
                }
            }
        }
    }
}