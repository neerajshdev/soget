package com.njsh.reelssaver.layer.ui.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njsh.reelssaver.App
import com.njsh.reelssaver.layer.domain.models.ReelModel
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.components.Advertisement
import com.njsh.reelssaver.layer.ui.components.InputUrlTaker
import com.njsh.reelssaver.layer.ui.components.SavableVideoCard
import com.njsh.reelssaver.layer.ui.theme.AppTheme


enum class ContentFetchingState {
    NOTHING, FETCHING, FETCHED
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun InstagramSaverPage(uiState: UiState) {
    var contentFetchingState by remember {
        mutableStateOf(
            ContentFetchingState.NOTHING
        )
    }
    var reelModel by remember { mutableStateOf<ReelModel?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
    ) {
        CenterAlignedTopAppBar(title = {
            Text(
                text = "Instagram",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f)
            )
        }, navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f)
            )
        })
        Spacer(modifier = Modifier.height(14.dp))

        // Takes input url from user & fetch the content
        InputUrlTaker(verifyInput = {
            it.matches(Regex("(http|https):\\/\\/.+"))
        }, onInput = {
            contentFetchingState = ContentFetchingState.FETCHING
            uiState.fetchInstagramReel(it) { result ->
                if (result.isSuccess) {
                    reelModel = result.getOrNull()!!
                    contentFetchingState = ContentFetchingState.FETCHED
                    println(reelModel)
                } else {
                    App.toast("something went wrong!")
                    contentFetchingState = ContentFetchingState.NOTHING
                }
            }
        })


        // while the content is fetching show a progress bar
        // if the content is fetched then show it else nothing
        AnimatedContent(
            targetState = contentFetchingState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) { contentFetchingState ->
            when (contentFetchingState) {
                ContentFetchingState.FETCHING -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                ContentFetchingState.FETCHED -> {
                    SavableVideoCard(thumbnailUrl = reelModel!!.imageUrl, onDownloadClick = {
                        uiState.download(reelModel!!)
                    }, modifier = Modifier.fillMaxSize())
                }
                else -> {}
            }
        }

        Advertisement(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .wrapContentSize()
        )
    }
}


@Preview
@Composable
private fun PInstagramSaverPage() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            InstagramSaverPage(UiState())
        }
    }
}

