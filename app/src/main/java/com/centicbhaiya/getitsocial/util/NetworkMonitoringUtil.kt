package com.centicbhaiya.getitsocial.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.mutableStateOf
import com.centicbhaiya.getitsocial.App

object NetworkMonitoringUtil : ConnectivityManager.NetworkCallback()
{
    private val TAG = NetworkMonitoringUtil::class.simpleName

    val isOnline  = mutableStateOf(false)

    private val networkReq by lazy {
        NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    private val conManager by lazy {
        App.instance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun registerNetworkCallbackEvents()
    {
        conManager.registerNetworkCallback(networkReq, this)
    }

    override fun onAvailable(network: Network)
    {
        isOnline.value = true
    }

    override fun onLost(network: Network)
    {
        isOnline.value = false
    }
}