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
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.App
import com.njsh.reelssaver.FirebaseKeys
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ViewModel
import com.njsh.reelssaver.ads.checkAndShowAd
import com.njsh.reelssaver.api.CallResult
import com.njsh.reelssaver.entity.EntityInstaReel
import com.njsh.reelssaver.navigation.Page
import com.njsh.reelssaver.ui.components.CircularProgressBar
import com.njsh.reelssaver.ui.components.InputPasteAndGet
import com.njsh.reelssaver.ui.components.NativeAdView
import com.njsh.reelssaver.ui.components.TopAppbar
import com.njsh.reelssaver.util.checkStoragePermission
import com.njsh.reelssaver.util.storagePermission
import kotlinx.coroutines.launch

class PageInstagram(private val navController: NavController) : Page("Instagram") {
    private val instagram = ViewModel.instagram
    private val inputUrl = InputPasteAndGet()
    private val isLoading = mutableStateOf(false)

    init {
        val parentModifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)

        val inputUrlModifier = Modifier.fillMaxWidth()

        val topAppBar = TopAppbar(pageTag)

        addContent {
            val scope = rememberCoroutineScope()
            val activity = LocalContext.current
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    topAppBar.drawContent()
                    val onDownloadClick: () -> Unit = {
                        scope.launch {
                            checkAndShowAd(activity) {
                                if (checkStoragePermission()) {
                                    instagram.reelState.value?.download()
                                } else {
                                    storagePermission(activity)
                                }
                            }
                        }
                    }
                    Column(modifier = parentModifier) {
                        inputUrl.drawContent(inputUrlModifier)
                        NativeAdView()
                        if (instagram.reelState.value != null) {
                            val reel = instagram.reelState.value!!
                            Reel(
                                reel = reel, modifier = Modifier.padding(top = 8.dp)
                            )
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
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "DOWNLOAD", Modifier.padding(vertical = 4.dp))
                                }
                            }
                        } else {
                            CircularProgressBar(isLoading)
                        }
                    }
                }

                SideEffect {
                    inputUrl.eventOnGetClick = {
                        scope.launch {
                            checkAndShowAd(activity) {
                                isLoading.value = true
                                val dsUserId =
                                    Firebase.remoteConfig.getString(FirebaseKeys.DS_USER_ID)
                                val sessionId =
                                    Firebase.remoteConfig.getString(FirebaseKeys.SESSION_ID)

                                instagram.getContent(
                                    inputUrl.text.value, dsUserId, sessionId
                                ) { result ->
                                    if (result is CallResult.Failed) {
                                        App.toast(result.msg)
                                    } else if (result is CallResult.Success) {
                                    }
                                    isLoading.value = false
                                }
                            }
                        }

                    }

                    inputUrl.eventOnPasteClick = {
                        scope.launch {
                            checkAndShowAd(activity) {
                                inputUrl.text.value = App.clipBoardData()
                            }
                        }
                    }
                }
            }

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
    private fun Reel(reel: EntityInstaReel, modifier: Modifier = Modifier) {
        AsyncImage(
            model = reel.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 360.dp)
        )
    }
}



