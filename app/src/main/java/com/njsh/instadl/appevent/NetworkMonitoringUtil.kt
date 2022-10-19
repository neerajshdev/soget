package com.njsh.instadl.appevent

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.njsh.instadl.App

class NetworkMonitoringUtil : ConnectivityManager.NetworkCallback()
{
    private val TAG = NetworkMonitoringUtil::class.simpleName

    private val networkReq by lazy {
        NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    private val conManager by lazy {
        App.instace().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun registerNetworkCallbackEvents()
    {
        conManager.registerNetworkCallback(networkReq, this)
    }

    override fun onAvailable(network: Network)
    {
        EventManager.getInstance().fire(ConnectionAvailable())
    }

    override fun onLost(network: Network)
    {
        EventManager.getInstance().fire(ConnectionLost())
    }
}