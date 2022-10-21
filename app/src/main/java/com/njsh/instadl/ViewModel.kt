package com.njsh.instadl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.njsh.instadl.api.CallResult
import com.njsh.instadl.api.FetchFacebookVideoImpl
import com.njsh.instadl.api.FetchInstaReelImpl
import com.njsh.instadl.entity.EntityFBVideo
import com.njsh.instadl.entity.EntityInstaReel
import com.njsh.instadl.util.isOnline

object ViewModel
{
    var isUserOnline = mutableStateOf( isOnline(App.instance()))
    val instagram = InstagramViewModel()
    val facebook = FacebookViewModel()
}


class InstagramViewModel
{
    val reelState: MutableState<EntityInstaReel?> = mutableStateOf(null)

    fun getContent(
        url: String, dsUserId: String, sessionId: String, callbacks: (CallResult<Nothing>) -> Unit
    )
    {
        val fetchImpl = FetchInstaReelImpl(url, dsUserId, sessionId)
        Executor.execute {
            fetchImpl.fetchReelData { result ->
                if (result is CallResult.Success)
                {
                    reelState.value = result.data
                } else if (result is CallResult.Failed)
                {
                    callbacks.invoke(CallResult.Failed(result.msg))
                }
            }
        }
    }
}


class FacebookViewModel
{
    val videoState: MutableState<EntityFBVideo?> = mutableStateOf(null)

    fun getContent(url: String, callback: (CallResult<*>) -> Unit)
    {
        // submit a task
        Executor.execute {
            val fetch = FetchFacebookVideoImpl(url)
            fetch.fetchVideo { result ->
                if (result is CallResult.Success)
                {
                    videoState.value = result.data
                    callback(result)
                } else if (result is CallResult.Failed)
                {
                    callback(result)
                }
            }
        }
    }
}

