package com.njsh.reelssaver

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.njsh.infinitelist.Datasource
import com.njsh.infinitelist.InfiniteList
import com.njsh.infinitelist.UniqueKey
import com.njsh.infinitelist.rememberInfiniteListState
import com.njsh.reelssaver.shorts.views.ScrollableShorts
import kotlin.random.Random

private const val TAG = "MainActivity.kt"

data class CustomData(val color: Color, val index: Int) : UniqueKey {
    override val key get() = index
    override fun toString(): String {
        return key.toString()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScrollableShorts()
        }
    }
}

