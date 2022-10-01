package com.njsh.myapp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.njsh.downloader.DBHelper
import com.njsh.myapp.ui.pages.YoutubePage
import com.njsh.myapp.ui.theme.MyappTheme

class MainActivity : ComponentActivity()
{
    private val TAG = javaClass.name
    private val youtubePage = YoutubePage()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            MyappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    youtubePage.Compose()
                }
            }
        }
    }
}