package com.gd.reelssaver.util

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.gd.reelssaver.AppPref
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.CompletableDeferred
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "Functions.Kt"

fun visitDocumentTree(root: DocumentFile, visitor: (DocumentFile) -> Int) {
    if (!root.exists()) {
        throw IllegalArgumentException("provided document path does not exists")
    }
    val children = root.listFiles()
    for (child in children) {
        if (child.isFile) { // question: should stop after visit this file
            if (visitor(child) == 0) break
        } else { // question: should explore this directory
            val ans = visitor(child)
            if (ans == 0) { // go in and break
                visitDocumentTree(child, visitor)
                break
            } else if (ans == 1) { // go in and don't break
                visitDocumentTree(child, visitor)
            } else { // skip
                //                console(com.njsh.reelssaver.TAG, "skipping dir ${child.name}")
            }
        }
    }
}


fun visitFileTree(root: File, visitor: (File) -> Int) {
    if (!root.exists()) {
        throw IllegalArgumentException("provided file path does not exists")
    }
    val children = root.listFiles()
    if (children != null) for (child in children) {
        if (child.isFile) { // question: should stop after visit this file
            if (visitor(child) == 0) break
        } else { // question: should explore this directory
            val ans = visitor(child)
            if (ans == 0) { // go in and break
                visitFileTree(child, visitor)
                break
            } else if (ans == 1) { // go in and don't break
                visitFileTree(child, visitor)
            } else { // skip
                //                    console(com.njsh.reelssaver.TAG, "skipping dir ${child.name}")
            }
        }
    }
}



fun checkStoragePermission(): Boolean {
    return ActivityCompat.checkSelfPermission(
        com.gd.reelssaver.App.instance(), Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        com.gd.reelssaver.App.instance(), Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}


fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }

    }

    return connectivityManager.activeNetworkInfo?.run {
        isAvailable || isConnectedOrConnecting
    } ?: false
}

suspend fun Firebase.fetchAndActivate() {
    val deferred = CompletableDeferred<Task<Boolean>>()
    remoteConfig.fetchAndActivate().addOnCompleteListener { deferred.complete(it) }
    val isComplete = deferred.await().isComplete

    AppPref.edit {
        putBoolean(AppPref.FIREBASE_FETCHED, isComplete)
    }
}

/**
 * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
 * @param context Context reference to get the TelephonyManager instance from
 * @return country code or null
 */
fun getUserCountry(context: Context): String? {
    try {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        if (simCountry != null && simCountry.length == 2) { // SIM country code is available
            return simCountry.lowercase()
        } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
            val networkCountry = tm.networkCountryIso
            if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                return networkCountry.lowercase()
            }
        }
    } catch (e: Exception) {
    }
    return null
}


fun download(title: String, url: String, description: String) {
    val downloadManager =
        com.gd.reelssaver.App.instance().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = Uri.parse(url)

    val req = DownloadManager.Request(uri)

    req.apply {
        setTitle(title)
        setDescription(description)
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
        setAllowedOverMetered(true)
        setAllowedOverRoaming(true)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setMimeType(com.gd.reelssaver.App.instance().contentResolver.getType(uri))
    }
    downloadManager.enqueue(req)
}

fun Long.toPrettyNum(): String {
    return when {
        this > 1000_000 -> {
            String.format("%0.1fm", this/1000_000f).replace(Regex("\\.0"), "")
        }
        this > 1000 -> {
            String.format("%.1fk", this/1000f).replace(Regex("\\.0"), "")
        }
        else -> {
            toString()
        }
    }
}


/**
 * Share a link
 */
fun share(url: String, context: Context) {
    val myIntent = Intent(Intent.ACTION_SEND)
    myIntent.type = "text/plain"
    val appLink = "https://play.google.com/store/apps/details?id=${com.gd.reelssaver.BuildConfig.APPLICATION_ID}"
    val sub: String = "All Video downloader"
    val body: String = "$url \n\n Hey! This app can download any instagram and facebook video. \n Download Now its free! \n $appLink \n"
    myIntent.putExtra(Intent.EXTRA_SUBJECT, sub)
    myIntent.putExtra(Intent.EXTRA_TEXT, body)
    context.startActivity(Intent.createChooser(myIntent, "Share Using"))
}

fun createFileName(postFix: String): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "${timestamp}_$postFix"
}


fun getVideoThumbnail(videoUrl: String): Bitmap? {
    val retriever =  MediaMetadataRetriever()
    var thumbnail : Bitmap? = null
    try {
        // Set video URL
        retriever.setDataSource(videoUrl, HashMap < String, String > ())
        // Get frame at the 1st second as the thumbnail
        thumbnail = retriever.getFrameAtTime (1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        // Use this thumbnail Bitmap as needed
    } catch (e: Exception) {
        e.printStackTrace();
        // Handle exceptions
    } finally {
        retriever.release()
    }
    return thumbnail
}


/**
 * Extracts the first url from given string
 */
fun findFirstUrl(input: String): String? {
    val regex = Regex("""\b(?:https?):\/\/\S+\b""")
    val matchResult = regex.find(input)
    return matchResult?.value
}