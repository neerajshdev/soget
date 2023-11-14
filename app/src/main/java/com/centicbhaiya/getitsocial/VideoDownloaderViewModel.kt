package com.centicbhaiya.getitsocial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.centicbhaiya.getitsocial.api.VideoDataFetch
import com.centicbhaiya.getitsocial.domain.models.VideoData
import com.centicbhaiya.getitsocial.ui.screens.isOk
import com.centicbhaiya.getitsocial.util.download
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoDownloaderViewModel : ViewModel() {
    private val _url: MutableStateFlow<String> = MutableStateFlow("")
    private val _fetchedVideoData: MutableStateFlow<VideoData?> = MutableStateFlow(null)
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val url: StateFlow<String> = _url
    val fetchedVideoData = _fetchedVideoData
    val isLoading = _isLoading

    private val baseUrl by lazy { Firebase.remoteConfig.getString(FirebaseKeys.BaseUrl) }
    private val api by lazy { VideoDataFetch(baseUrl) }

    fun fetchVideoData() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = api.getVideoData(url.value)
            if (result.isSuccess) {
                _fetchedVideoData.value = result.getOrThrow()
            }
            _isLoading.value = false
        }
    }

    fun setUrl(value: String) {
        _url.value = value
    }

    fun downloadVideo(onSuccess: suspend () -> Unit) {
        viewModelScope.launch {
            fetchedVideoData.value?.let {
                if (it.isOk()) {
                    download(
                        title = "Video_Downloader",
                        url = it.video_url,
                        description = "Video Downloader"
                    )
                    onSuccess()
                }
            }
        }
    }
}