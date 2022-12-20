package com.njsh.reelssaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.njsh.reelssaver.layer.ui.PageHost

private const val TAG = "MainActivity.kt"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PageHost(modifier = Modifier.fillMaxSize())
        }
    }
}




