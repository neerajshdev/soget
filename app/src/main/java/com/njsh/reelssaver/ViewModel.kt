package com.njsh.reelssaver

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.njsh.reelssaver.api.CallResult
import com.njsh.reelssaver.api.FetchFacebookVideoImpl
import com.njsh.reelssaver.api.FetchInstaReelImpl
import com.njsh.reelssaver.entity.EntityFBVideo
import com.njsh.reelssaver.entity.EntityInstaReel
import com.njsh.reelssaver.util.isOnline

object ViewModel {
    val isUserOnline by lazy { mutableStateOf(isOnline(App.instance())) }
    val instagram by lazy { InstagramViewModel() }
    val facebook by lazy { FacebookViewModel() }
}


class InstagramViewModel {
    val reelState: MutableState<EntityInstaReel?> = mutableStateOf(null)

    fun getContent(
        url: String, dsUserId: String, sessionId: String, callbacks: (CallResult<Nothing>) -> Unit
    ) {
        val fetchImpl = FetchInstaReelImpl(url, dsUserId, sessionId)
        Executor.execute {
            fetchImpl.fetchReelData { result ->
                if (result is CallResult.Success) {
                    reelState.value = result.data
                } else if (result is CallResult.Failed) {
                    callbacks.invoke(CallResult.Failed(result.msg))
                }
            }
        }
    }
}


class FacebookViewModel {
    val videoState: MutableState<EntityFBVideo?> = mutableStateOf(null)

    fun getContent(url: String, callback: (CallResult<*>) -> Unit) { // submit a task
        Executor.execute {
            val fetch = FetchFacebookVideoImpl(url)
            fetch.fetchVideo { result ->
                if (result is CallResult.Success) {
                    videoState.value = result.data
                    callback(result)
                } else if (result is CallResult.Failed) {
                    callback(result)
                }
            }
        }
    }
}

