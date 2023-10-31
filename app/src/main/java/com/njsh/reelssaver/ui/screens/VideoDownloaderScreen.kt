package com.njsh.reelssaver.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.FirebaseKeys
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ads.InterstitialAdManager
import com.njsh.reelssaver.api.VideoDataFetch
import com.njsh.reelssaver.domain.models.VideoData
import com.njsh.reelssaver.ui.components.BannerSmallNativeAd
import com.njsh.reelssaver.ui.theme.AppTheme
import com.njsh.reelssaver.ui.theme.useDarkTheme
import com.njsh.reelssaver.util.download
import kotlinx.coroutines.launch
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDownloaderScreen(navController: NavController? = null) {
    var videoLink by remember { mutableStateOf(TextFieldValue("")) }
    var fetchedVideo by remember { mutableStateOf<VideoData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isValidLink by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val api = remember {
        VideoDataFetch(
            Firebase.remoteConfig.getString(FirebaseKeys.BaseUrl)
        )
    }

    val isWritePermissionGranted = checkAndRequestStoragePermission()

    @Composable
    fun TopBar() {
        TopAppBar(
            title = { Text("Video Downloader") },
            actions = {  // Adding the theme change icon button
                IconButton(onClick = { navController?.navigate(RouteName.HowToScreen) }) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "How to use")
                }

                IconButton(onClick = {
                    scope.launch {
                        useDarkTheme = useDarkTheme.not()
                        val message =
                            if (useDarkTheme) "Switched to Dark Theme" else "Switched to Light Theme"
                        snackbarHostState.showSnackbar(message)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = if (useDarkTheme) R.drawable.baseline_dark_mode_24 else R.drawable.baseline_light_mode_24),
                        contentDescription = "Change Theme",
                        tint = colorScheme.onSurface
                    )
                }
            },

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.surface,
                titleContentColor = colorScheme.onSurface
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
    fun FetchButton(isValidLink: Boolean, onFetchClick: () -> Unit) {
        Button(
            onClick = onFetchClick,
            enabled = isValidLink,
            colors = ButtonDefaults.buttonColors(contentColor = colorScheme.primary)
        ) {
            Text("Fetch Video Data", color = colorScheme.onPrimary)
        }
    }

    @Composable
    fun LoadingIndicator() {
        CircularProgressIndicator(color = colorScheme.primary)
    }

    @Composable
    fun VideoThumbnail(videoPreviewLink: String) {
        AsyncImage(
            model = videoPreviewLink,
            contentDescription = "Video Preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }

    @Composable
    fun DownloadButton(onDownloadClick: () -> Unit) {
        Button(
            enabled = isWritePermissionGranted,
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
                val data = clipboardData.text.trim()
                videoLink = TextFieldValue(data)
                isValidLink = isValidUrl(data)
            }
        }) {
            Icon(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = "Paste from Clipboard",
                tint = colorScheme.onSurface
            )
        }
    }

    Scaffold(
        topBar = { TopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))

            InstructionsText()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VideoLinkInput(videoLink.text, onVideoLinkChange = { value ->
                    videoLink = TextFieldValue(value.trim())
                    isValidLink = isValidUrl(videoLink.text)
                })
                ClipboardButton()
            }

            Spacer(modifier = Modifier.height(16.dp))

            FetchButton(isValidLink = isValidLink, onFetchClick = {
                if (InterstitialAdManager.willShowAd()) {
                    InterstitialAdManager.showAd()
                }

                scope.launch {
                    isLoading = true
                    val result = api.getVideoData(url = videoLink.text)
                    if (result.isSuccess) {
                        fetchedVideo = result.getOrThrow()
                    }
                    isLoading = false
                }
            })

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                LoadingIndicator()
            } else {
                fetchedVideo?.let {
                    VideoThumbnail(it.thumbnail_url)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (fetchedVideo != null) {
                DownloadButton(onDownloadClick = {
                    download(
                        title = "Video_Downloader",
                        url = fetchedVideo!!.video_url,
                        description = "Video Downloader"
                    )
                })
            }

            Spacer(modifier = Modifier.weight(0.3f))

            BannerSmallNativeAd(modifier = Modifier.fillMaxWidth())
        }
    }
}

fun isValidUrl(urlString: String): Boolean {
    return try {
        URL(urlString)
        true
    } catch (e: Exception) {
        false
    }
}

@Composable
fun checkAndRequestStoragePermission(): Boolean {
    val context = LocalContext.current
    var isPermissionGranted by remember { mutableStateOf(false) }

    // Initialize the ActivityResultLauncher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isPermissionGranted = isGranted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Check if permission is already granted
            isPermissionGranted =
                context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            if (!isPermissionGranted) {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            // For Android Q and above, handle as required (e.g., using Scoped Storage)
            isPermissionGranted = true
        }
    }

    return isPermissionGranted
}


@Preview
@Composable
fun PreviewVideoDownloaderScreen() {
    AppTheme {
        VideoDownloaderScreen()
    }
}
