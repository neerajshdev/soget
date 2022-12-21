package com.njsh.reelssaver.layer.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.ktx.Firebase
import com.njsh.reelssaver.App
import com.njsh.reelssaver.AppPref
import com.njsh.reelssaver.layer.domain.models.FbVideoModel
import com.njsh.reelssaver.layer.domain.models.ReelModel
import com.njsh.reelssaver.util.fetchAndActivate
import com.njsh.reelssaver.util.isOnline
import kotlinx.coroutines.delay

class UiState {
    var isOnline by mutableStateOf(false)

    private val mShortVideoModel: ShortVideoModel by lazy { ShortVideoModel() }
    private lateinit var mNetworkCallback : ConnectivityManager.NetworkCallback

    fun initState() {
        isOnline = isOnline(App.instance())
        val connectivityManager = App.instance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val netReq = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()


        mNetworkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isOnline = true
                }

                override fun onLost(network: Network) {
                    isOnline = false
                }
            }

        connectivityManager.registerNetworkCallback(netReq, mNetworkCallback)
    }

    fun clearState() {
        val connectivityManager = App.instance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(mNetworkCallback)
    }

    suspend fun syncFirebase() {
        Firebase.fetchAndActivate()
    }

    suspend fun waitUntilOnline() {
        while (!isOnline) {
            delay(1000)
        }
    }

    fun isFirebaseSync() = AppPref.pref.getBoolean(AppPref.FIREBASE_FETCHED, false)

    suspend fun fetchInstagramReel(url: String): ReelModel {
        TODO("Not yet implemented")
    }

    suspend fun fetchFacebookVideo(url: String): FbVideoModel {
        TODO("Not yet implemented")
    }

    fun download(url: String, title: String, description: String) {
        TODO("Not yet implemented")
    }

    fun getShortVideoModel(): ShortVideoModel {
        return mShortVideoModel
    }
}


class ShortVideoModel