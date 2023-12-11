package com.gd.reelssaver.ui.composables

import android.content.Context
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.gd.reelssaver.model.VideoData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private const val TAG = "ComposeWebView"

class MyJavaScriptInterface {
    @JavascriptInterface
    fun log(message: String) {
        Log.d("MyJavaScriptInterface", message)
    }
}

@Composable
fun ComposeWebView(
    modifier: Modifier = Modifier,
    initialUrl: String,
    webView: WebView? = null,
    onCreate: (WebView) -> Unit,
    onPageLoad: (url: String) -> Unit,
) {
    var backEnabled by remember { mutableStateOf(false) }

    Box {
        AndroidView(modifier = modifier, factory = {
            webView ?: WebView(it).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
                )

                addJavascriptInterface(MyJavaScriptInterface(), "Android")

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    javaScriptCanOpenWindowsAutomatically = true
                    setSupportMultipleWindows(true)
                    databaseEnabled = true
                }

                settings.userAgentString =
                    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36"

                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptThirdPartyCookies(this, true)

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        // Load and inject JavaScript from the assets
                        val script = loadJavaScriptFromAsset(view.context, "script.js")
                        view.evaluateJavascript(script, null)
                        onPageLoad(url)

                        Log.d(TAG, "onPageFinished: $url")

                        backEnabled = view.canGoBack()
                        Log.d(TAG, "back enabled: $backEnabled")
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView, request: WebResourceRequest
                    ): Boolean {
                        Log.d(TAG, "loading url: ${request.url}")
                        return request.url.scheme?.startsWith("http")?.not() ?: false
                    }
                }

                loadUrl(initialUrl)
                onCreate(this)
            }
        }, update = {}
        )
    }

    BackHandler(backEnabled) {
        webView?.goBack()
        Log.d(TAG, "ComposeWebView: goBack()")
    }
}


fun loadJavaScriptFromAsset(context: Context, fileName: String): String {
    return try {
        val inputStream = context.assets.open(fileName)
        inputStream.bufferedReader().use(BufferedReader::readText)
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}


suspend fun searchVideoElement(webView: WebView): List<VideoData> {
    return suspendCoroutine { cont ->

        val url = URL(webView.url)
        val jsFunc = when {
            url.host.endsWith("facebook.com") -> "getAllVisibleFbVideo();"
            url.host.endsWith("instagram.com") -> "getAllVisibleIGVideo();"
            else -> null
        }

        if (jsFunc != null) {
            webView.evaluateJavascript(jsFunc) { result ->
                val data = result.unescapeJson()
                Log.d(TAG, "searchVideoElement: $data")
                try {
                    val fbVideoData: List<VideoData> =
                        Gson().fromJson(data, object : TypeToken<List<VideoData>>() {}.type)
                    Log.d(TAG, "fbVideoData: $fbVideoData")
                    cont.resume(fbVideoData)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    cont.resume(emptyList())
                }
            }
        } else {
            cont.resume(emptyList())
        }
    }
}

fun String.unescapeJson(): String {
    return this.replace("\\\"", "\"").replace("\\\\", "\\").removeSurrounding("\"", "\"")
}