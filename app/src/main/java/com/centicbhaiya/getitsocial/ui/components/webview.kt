package com.centicbhaiya.getitsocial.ui.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.centicbhaiya.getitsocial.model.FBVideoData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException


private const val TAG = "ComposeWebView"

class MyJavaScriptInterface {
    @JavascriptInterface
    fun log(message: String) {
        Log.d("MyJavaScriptInterface", message)
    }
}


@Composable
fun ComposeWebView(modifier: Modifier = Modifier, url: String) {
    var webView: WebView? = null

    LaunchedEffect(key1 = url) {
        webView?.loadUrl(url)
        Log.d(TAG, "userAgentString: ${webView?.settings?.userAgentString}")
    }


    Box {
        AndroidView(modifier = modifier, factory = {
            WebView(it).apply {
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
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView, request: WebResourceRequest
                    ): Boolean {
                        Log.d(TAG, "loading url: ${request.url}")
                        return request.url.scheme?.startsWith("http")?.not() ?: false
                    }
                }
            }
        }, update = {
            webView = it
        })

        Button(
            onClick = { searchVideoElement(webView!!) },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = "Get Info")
        }
    }


    BackHandler {
        if (webView?.canGoBack()!!) webView?.goBack()
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


fun searchVideoElement(webView: WebView) {
    webView.evaluateJavascript("getAllVisibleFbVideo();") { result ->
        val data = result.unescapeJson()
        Log.d(TAG, "searchVideoElement: $data")
        try {
            val fbVideoData: List<FBVideoData> =
                Gson().fromJson(data, object : TypeToken<List<FBVideoData>>() {}.type)
            Log.d(TAG, "fbVideoData: $fbVideoData")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun String.unescapeJson(): String {
    return this.replace("\\\"", "\"").replace("\\\\", "\\").removeSurrounding("\"", "\"")
}