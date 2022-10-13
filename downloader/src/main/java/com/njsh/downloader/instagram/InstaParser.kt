package com.njsh.downloader.instagram

import org.jsoup.Jsoup
import java.net.URL

class InstaParser(val url: String)
{
    fun fetch()
    {
        val document = Jsoup.parse(url)
    }
}