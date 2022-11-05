package com.njsh.reelssaver.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.ui.theme.AppTheme


@Preview
@Composable
fun PreviewInstagramScreen() {
    AppTheme {
        val page = PageInstagram(rememberNavController())
        page.drawContent()
    }
}


@Preview
@Composable
fun PreviewFacebookDownloaderScreen() {
    AppTheme {
        val page = PageFacebookVideo(rememberNavController())
        page.drawContent()
    }
}