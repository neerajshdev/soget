package com.gd.getitsocial

import com.desidev.downloader.model.Download
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {

    @Test
    fun sortingTest() {
        val list = listOf(LocalDateTime.now(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1))

        val sortedList = list.sortedWith(compareByDescending<LocalDateTime> { it.toEpochSecond(
            ZoneOffset.UTC) })

        for (date in list) {
            println(date)
        }
        println()

        for (date in sortedList) {
            println(date)
        }
    }
}