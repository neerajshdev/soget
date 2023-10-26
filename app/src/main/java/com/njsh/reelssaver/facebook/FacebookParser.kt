package com.njsh.reelssaver.facebook

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.MalformedURLException
import java.net.URL

class FacebookParser {

    suspend fun findUrls(url: String): List<String> {
        val client = createHttpClient()
        val htmlContent = fetchHtmlContent(client, url)
        val scriptData = extractScriptContents(htmlContent)
        return scriptData.mapNotNull { extractUrlsFromJson(it) }.flatten()
    }

    fun createHttpClient(): HttpClient {
        return HttpClient(CIO)
    }

    suspend fun fetchHtmlContent(client: HttpClient, url: String): String {
        val response: HttpResponse = client.get(url) {
            headers {
                append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                append("Accept-Encoding", "gzip, deflate, br")
                append("Cookie", "")
                append("Accept-Language", "en-US,en;q=0.9")
                append("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                append("Viewport-Width", "1064")
                append("Upgrade-Insecure-Requests", "1")
                append("Sec-Fetch-User", "?1")
                append("Sec-Fetch-Site", "same-origin")
                append("Sec-Fetch-Mode", "navigate")
                append("Sec-Fetch-Dest", "document")
                append("Sec-Ch-Ua-Platform-Version", "10.0.0")
                append("Sec-Ch-Ua-Platform", "Windows")
                append("Sec-Ch-Ua-Model", "")
                append("Sec-Ch-Ua-Mobile", "?0")
                append("Sec-Ch-Ua-Full-Version-List", "\"Chromium\";v=\"118.0.5993.89\", \"Google Chrome\";v=\"118.0.5993.89\", \"Not=A?Brand\";v=\"99.0.0.0\"")
                append("Sec-Ch-Ua", "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"")
                append("Sec-Ch-Prefers-Color-Scheme", "light")
                append("Dpr", "1")
                append("Cache-Control", "max-age=0")
            }
        }
        return response.bodyAsText()
    }

    fun extractScriptContents(htmlContent: String): List<String> {
        val document: Document = Jsoup.parse(htmlContent)
        val scriptTags: List<Element> = document.select("script")

        return scriptTags.mapNotNull { scriptTag ->
            val scriptData = scriptTag.data()
            if (scriptData.isNotBlank() && scriptData.trim().startsWith("{")) {
                scriptData
            } else {
                null
            }
        }
    }

    fun extractUrlsFromJson(jsonContent: String): List<String>? {
        return try {
            val jsonElement = Json.parseToJsonElement(jsonContent)
            val list = mutableListOf<String>()
            extractUrlsFromJson(jsonElement, list)
            list
        } catch (e: Exception) {
            // Handle JSON parsing exceptions
            println("An error occurred during JSON parsing: ${e.message}")
            null
        }
    }


    fun extractUrlsFromJson(jsonElement: JsonElement, list: MutableList<String>) {
        when (jsonElement) {
            is JsonPrimitive -> {
                val maybeUrl = jsonElement.content
                if (isValidUrl(maybeUrl)) {
                    if (URL(maybeUrl).file.endsWith("mp4")) list.add(maybeUrl)
                }
            }

            is JsonArray -> {
                for (elem in jsonElement) {
                    extractUrlsFromJson(elem, list)
                }
            }

            is JsonObject -> {
                for ((key, value) in jsonElement) {
                    if (key.contentEquals("video")) {
                        println(jsonElement.toString())
                    }

                    extractUrlsFromJson(value, list)
                }
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: MalformedURLException) {
            false
        }
    }

}