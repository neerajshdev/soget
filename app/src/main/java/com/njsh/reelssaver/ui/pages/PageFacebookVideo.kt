package com.njsh.reelssaver.ui.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.njsh.reelssaver.App
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ViewModel
import com.njsh.reelssaver.ads.checkAndShowAd
import com.njsh.reelssaver.api.CallResult
import com.njsh.reelssaver.entity.EntityFBVideo
import com.njsh.reelssaver.navigation.Page
import com.njsh.reelssaver.ui.components.CircularProgressBar
import com.njsh.reelssaver.ui.components.InputPasteAndGet
import com.njsh.reelssaver.ui.components.NativeAdView
import com.njsh.reelssaver.ui.components.TopAppbar
import com.njsh.reelssaver.util.checkStoragePermission
import com.njsh.reelssaver.util.storagePermission
import kotlinx.coroutines.launch


class PageFacebookVideo(private val navController: NavController) : Page("Facebook video Downloader") {
    private val facebook = ViewModel.facebook
    private val inputUrl = InputPasteAndGet()
    private var isLoading = mutableStateOf(false)

    init {
        val inputUrlModifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        val topAppbar = TopAppbar(pageTag)

        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                val context = LocalContext.current
                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())) {
                    topAppbar.drawContent()

                    val onDownloadClick: () -> Unit = {
                        if (checkStoragePermission()) {
                            facebook.videoState.value?.download()
                        } else {
                            storagePermission(context)
                        }
                    }

                    Column {
                        inputUrl.drawContent(inputUrlModifier)
                        NativeAdView()
                        Spacer(modifier = Modifier.height(8.dp))
                        if (facebook.videoState.value != null) {
                            val reel = facebook.videoState.value!!
                            Thumbnail(reel = reel, modifier = Modifier.weight(1f))
                            Button(
                                onClick = onDownloadClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_round_downloaad),
                                        contentDescription = null
                                    )
                                    Text(text = "DOWNLOAD", Modifier.padding(vertical = 4.dp))
                                }
                            }
                        } else { // show content load on press get button
                            CircularProgressBar(isLoading)
                        }
                    }
                }

                val scope = rememberCoroutineScope()
                SideEffect {
                    inputUrl.eventOnGetClick = {
                        scope.launch {
                            checkAndShowAd(context) {
                                isLoading.value = true
                                facebook.getContent(inputUrl.text.value) { result ->
                                    if (result is CallResult.Success) {
                                    } else if (result is CallResult.Failed) {
                                        App.toast(result.msg)
                                    }
                                    isLoading.value = false
                                }
                            }
                        }
                    }

                    inputUrl.eventOnPasteClick = {
                        scope.launch {
                            checkAndShowAd(context) {
                                inputUrl.text.value = App.clipBoardData()
                            }
                        }
                    }
                }
            }

            val activity = LocalContext.current
            val scope = rememberCoroutineScope()
            if (doBackPressAds) BackHandler {
                scope.launch {
                    checkAndShowAd(activity) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }


    @Composable
    private fun Thumbnail(reel: EntityFBVideo, modifier: Modifier = Modifier) {
        AsyncImage(
            model = reel.thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 360.dp)
        )
    }
}