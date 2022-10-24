package com.njsh.instadl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.njsh.instadl.ui.pages.ActivityContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityContent.Content()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Activity destroy")
    }
}