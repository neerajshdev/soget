package com.njsh.instadl.ui.pages

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.instadl.App
import com.njsh.instadl.FirebaseKeys
import com.njsh.instadl.R
import com.njsh.instadl.ViewModel
import com.njsh.instadl.ads.checkAndShowAd
import com.njsh.instadl.api.CallResult
import com.njsh.instadl.entity.EntityInstaReel
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.CircularProgressBar
import com.njsh.instadl.ui.components.InputPasteAndGet
import com.njsh.instadl.ui.components.RightCurvedHeading
import com.njsh.instadl.ui.theme.AppTheme
import com.njsh.instadl.util.checkStoragePermission
import com.njsh.instadl.util.storagePermission

class PageInstagram : Page("Instagram") {
    private val instagram = ViewModel.instagram
    private val inputUrl = InputPasteAndGet()
    private val isLoading = mutableStateOf(false)

    init {
        val parentModifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        val inputUrlModifier = Modifier.fillMaxWidth()

        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                val activity = LocalContext.current as Activity
                Column {
                    RightCurvedHeading(
                        label = "ALL VIDEO DOWNLOADER", modifier = Modifier.padding(vertical = 4.dp)
                    )

                    val onDownloadClick: () -> Unit = {
                        checkAndShowAd(activity) {
                            if (checkStoragePermission()) {
                                instagram.reelState.value?.download()
                            } else {
                                storagePermission(activity)
                            }
                        }
                    }

                    Column(modifier = parentModifier) {
                        inputUrl.Compose(inputUrlModifier)
                        if (instagram.reelState.value != null) {
                            val reel = instagram.reelState.value!!
                            Reel(reel = reel, modifier = Modifier.weight(1f))
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

                SideEffect() {
                    inputUrl.eventOnGetClick = {
                        checkAndShowAd(activity) {
                            isLoading.value = true
                            val dsUserId = Firebase.remoteConfig.getString(FirebaseKeys.DS_USER_ID)
                            val sessionId = Firebase.remoteConfig.getString(FirebaseKeys.SESSION_ID)

                            instagram.getContent(
                                inputUrl.text.value, dsUserId, sessionId
                            ) { result ->
                                if (result is CallResult.Failed) {
                                    App.toast(result.msg)
                                } else if (result is CallResult.Success) {
                                    isLoading.value = false
                                }
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
        }
    }


    @Composable
    private fun Reel(reel: EntityInstaReel, modifier: Modifier = Modifier) {
        AsyncImage(
            model = reel.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxWidth()
        )
    }
}


@Preview
@Composable
fun PrevInstagramPage() {
    val page = PageInstagram()
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            page.drawContent()
        }
    }
}