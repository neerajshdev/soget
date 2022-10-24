package com.njsh.instadl.ui.pages

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.njsh.instadl.App
import com.njsh.instadl.MainActivity
import com.njsh.instadl.R
import com.njsh.instadl.ViewModel
import com.njsh.instadl.ads.NativeAdLoader
import com.njsh.instadl.ads.checkAndShowAd
import com.njsh.instadl.api.CallResult
import com.njsh.instadl.entity.EntityFBVideo
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.CircularProgressBar
import com.njsh.instadl.ui.components.InputPasteAndGet
import com.njsh.instadl.ui.components.NativeAdView
import com.njsh.instadl.ui.components.RightCurvedHeading
import com.njsh.instadl.util.checkStoragePermission
import com.njsh.instadl.util.storagePermission


class PageFacebookVideo(private val navController: NavController) : Page("Facebook video Downloader") {
    private val facebook = ViewModel.facebook
    private val inputUrl = InputPasteAndGet()
    private var isLoading = mutableStateOf(false)

    init {
        val parentModifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        val inputUrlModifier = Modifier.fillMaxWidth()

        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                val activity = LocalContext.current as Activity
                Column(modifier = parentModifier.verticalScroll(rememberScrollState())) {
                    RightCurvedHeading(
                        label = pageTag, modifier = Modifier.padding(vertical = 4.dp)
                    )

                    val onDownloadClick: () -> Unit = {
                        if (checkStoragePermission()) {
                            facebook.videoState.value?.download()
                        } else {
                            storagePermission(activity)
                        }
                    }

                    Column {
                        inputUrl.Compose(inputUrlModifier)
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

                SideEffect {
                    inputUrl.eventOnGetClick = {
                        checkAndShowAd(activity) {
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

                    inputUrl.eventOnPasteClick = {
                        checkAndShowAd(activity) {
                            inputUrl.text.value = App.clipBoardData()
                        }
                    }
                }
            }

            val activity = LocalContext.current as MainActivity
            BackHandler {
                checkAndShowAd(activity) {
                    navController.popBackStack()
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