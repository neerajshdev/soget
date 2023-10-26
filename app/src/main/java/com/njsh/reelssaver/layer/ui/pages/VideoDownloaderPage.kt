package com.njsh.reelssaver.layer.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njsh.reelssaver.R
import com.njsh.reelssaver.layer.ui.theme.AppTheme
import com.njsh.reelssaver.layer.ui.theme.useDarkTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDownloaderScreen() {
    var videoLink by remember { mutableStateOf(TextFieldValue("")) }
    var videoPreview by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showDisclaimer by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    @Composable
    fun TopBar() {
        TopAppBar(title = { Text("Video Downloader") },
            actions = {  // Adding the theme change icon button
                IconButton(onClick = {
                    useDarkTheme = useDarkTheme.not()
                    scope.launch {
                        val message =
                            if (useDarkTheme) "Switch to DarkTheme" else "Switch to LightTheme"
                        snackbarHostState.showSnackbar(message)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = if (useDarkTheme) R.drawable.baseline_light_mode_24 else R.drawable.baseline_dark_mode_24),
                        contentDescription = "Change Theme",
                        tint = colorScheme.onSurface
                    )
                }
            },

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.surface, titleContentColor = colorScheme.onSurface
            )
        )
    }


    @Composable
    fun InstructionsText() {
        Text(
            text = "Paste the video link below:",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurface
        )
    }

    @Composable
    fun VideoLinkInput(videoLink: String, onVideoLinkChange: (String) -> Unit) {
        OutlinedTextField(
            value = videoLink, onValueChange = onVideoLinkChange,

            label = { Text("Video Link") },

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
                cursorColor = colorScheme.primary,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.onSurfaceVariant,
            )
        )
    }

    @Composable
    fun FetchButton(onFetchClick: () -> Unit) {
        Button(
            onClick = onFetchClick,
            colors = ButtonDefaults.buttonColors(contentColor = colorScheme.primary)
        ) {
            Text("Fetch Video Data", color = colorScheme.onPrimary)
        }
    }

    @Composable
    fun LoadingIndicator(isLoading: Boolean) {
        if (isLoading) {
            CircularProgressIndicator(color = colorScheme.primary)
        }
    }

    @Composable
    fun VideoPreview(videoPreview: ImageBitmap?) {
        videoPreview?.let {
            Image(
                bitmap = it,
                contentDescription = "Video Preview",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    fun DownloadButton(onDownloadClick: () -> Unit) {
        Button(
            onClick = onDownloadClick,
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Text("Download Video", color = colorScheme.onPrimary)
        }
    }


    @Composable
    fun Disclaimer(showDisclaimer: Boolean) {
        if (showDisclaimer) {
            Text(
                text = """
                Downloading videos without permission is illegal. Ensure you have the right to download and use content from platforms like Facebook and Instagram. This tool is meant for personal use only, and we are not responsible for any misuse or copyright violations.
            """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }
    }


    @Composable
    fun ClipboardButton() {
        IconButton(onClick = {
            val clipboardData = clipboardManager.getText()
            if (!clipboardData.isNullOrBlank()) {
                videoLink = TextFieldValue(clipboardData)
            }
        }) {
            Icon(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = "Paste from Clipboard",
                tint = colorScheme.onSurface
            )
        }
    }

    Scaffold(topBar = {
        TopBar()
    }, snackbarHost = {
        snackbarHostState
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))

            InstructionsText()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VideoLinkInput(videoLink.text,
                    onVideoLinkChange = { value -> videoLink = TextFieldValue(value) })
                ClipboardButton()
            }

            Spacer(modifier = Modifier.height(16.dp))

            FetchButton(onFetchClick = { /* Fetch logic */ })

            Spacer(modifier = Modifier.weight(0.2f))

            if (isLoading) {
                LoadingIndicator(isLoading)
            } else {
                VideoPreview(videoPreview)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (videoPreview != null) {
                DownloadButton(onDownloadClick = { /* Download logic */ })
            }

            Spacer(modifier = Modifier.weight(0.3f))

            Disclaimer(showDisclaimer = true)
        }
    }
}

@Preview()
@Composable
fun PreviewVideoDownloaderScreen() {
    AppTheme {
        VideoDownloaderScreen()
    }
}
