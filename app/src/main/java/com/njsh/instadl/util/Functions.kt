package com.njsh.instadl.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.njsh.instadl.App
import com.njsh.instadl.AppPref
import kotlinx.coroutines.CompletableDeferred
import java.io.File

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
                //                console(TAG, "skipping dir ${child.name}")
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
                //                    console(TAG, "skipping dir ${child.name}")
            }
        }
    }
}


fun storagePermission(context: Context) {
    Dexter.withContext(context).withPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).withListener(object : MultiplePermissionsListener {
        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
            Log.d(TAG, "onPermissionsChecked: ${p0?.grantedPermissionResponses}")
        }

        override fun onPermissionRationaleShouldBeShown(
            p0: MutableList<PermissionRequest>?, p1: PermissionToken?
        ) {
            TODO("Not yet implemented")
        }

    }).check()
}


fun checkStoragePermission(): Boolean {
    return ActivityCompat.checkSelfPermission(
        App.instance(), Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        App.instance(), Manifest.permission.WRITE_EXTERNAL_STORAGE
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


