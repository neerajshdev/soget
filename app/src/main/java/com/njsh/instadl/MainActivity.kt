package com.njsh.instadl

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.njsh.instadl.ui.pages.ActivityContent

class MainActivity : ComponentActivity()
{
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
           ActivityContent.Content()
        }
    }
}