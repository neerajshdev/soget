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
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.App
import com.njsh.reelssaver.AppPref
import com.njsh.reelssaver.FirebaseKeys
import com.njsh.reelssaver.layer.data.ShortVideoRepositoryImpl
import com.njsh.reelssaver.layer.domain.data.ShortVideoRepository
import com.njsh.reelssaver.layer.domain.models.FbVideoModel
import com.njsh.reelssaver.layer.domain.models.ReelModel
import com.njsh.reelssaver.layer.domain.models.ShortVideoModel
import com.njsh.reelssaver.layer.domain.use_cases.FetchFBVideoUseCase
import com.njsh.reelssaver.layer.domain.use_cases.FetchReelUseCase
import com.njsh.reelssaver.util.download
import com.njsh.reelssaver.util.fetchAndActivate
import com.njsh.reelssaver.util.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class UiState {
    var isOnline by mutableStateOf(false)
    private lateinit var mNetworkCallback: ConnectivityManager.NetworkCallback
    val shortVideoState by lazy { ShortVideoState() }

    fun initState() {
        isOnline = isOnline(App.instance())
        val connectivityManager =
            App.instance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val netReq = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
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
        val connectivityManager =
            App.instance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun fetchInstagramReel(url: String, result: (Result<ReelModel>) -> Unit) = FetchReelUseCase(
        url = url,
        dsUserId = Firebase.remoteConfig.getString(FirebaseKeys.DS_USER_ID),
        sessionId = Firebase.remoteConfig.getString(FirebaseKeys.SESSION_ID)
    ).invoke(result)

    fun fetchFacebookVideo(
        url: String, result: (Result<FbVideoModel>) -> Unit
    ) = FetchFBVideoUseCase(url).invoke({
        result(Result.success(it))
    }, {
        result(Result.failure(it))
    })

    fun download(reelModel: ReelModel) {
        TODO()
    }

    fun download(fbVideoModel: FbVideoModel) {
        TODO()
    }

    fun getClipBoardText() = App.clipBoardData()

    class ShortVideoState internal constructor() {
        private val repository: ShortVideoRepository = ShortVideoRepositoryImpl()

        fun download(shortVideo: ShortVideoModel) {
            download(
                shortVideo.title, shortVideo.videoUrl, "short video status"
            )
        }

        suspend fun loadVideos(offset: Long, limit: Int) = withContext(Dispatchers.IO) {
            repository.get(offset, limit).also {
                println("loaded from repo: ${it.size}")
            }
        }
    }
}