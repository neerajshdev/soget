package com.njsh.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.njsh.myapp.ui.pages.WhatsStatusPage
import com.njsh.myapp.ui.theme.MyappTheme
import com.njsh.youtube.YtParser

class MainActivity : ComponentActivity() {

    private val whatsStatusPage = WhatsStatusPage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    whatsStatusPage.Compose()
                }
            }
        }
    }
}