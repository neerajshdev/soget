package com.njsh.reelssaver

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import com.njsh.infinitelist.Datasource
import com.njsh.infinitelist.InfiniteList
import kotlin.random.Random

private const val TAG = "MainActivity.kt"

data class CustomData(val color: Color, val index: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val datasource = object : Datasource<CustomData>() {
            val random = Random(System.currentTimeMillis())

            override fun onCreate(index: Int): CustomData {
                Log.d(TAG, "create data for $index")
                val color = Color(random.nextInt()).copy(alpha = 1.0f)
                return CustomData(color, index)
            }
        }

        setContent {
            InfiniteList(datasource = datasource) { data ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = data.color)
                ) {
                    Text(text = "${data.index}")
                }
            }

           /* SubCompose(key = 0) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red))
                DisposableEffect(key1 = Unit, effect = {
                    Log.d(TAG, "effect run")
                    onDispose {
                        Log.d(TAG, "onDispose")
                    }
                })
            }*/

        }
    }
}


@Composable
fun SubCompose(key: Int, content: @Composable () -> Unit) {
    SubcomposeLayout(modifier = Modifier.fillMaxSize()) { constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            subcompose(key, content).map { it.measure(constraints) }
        }
    }
}

