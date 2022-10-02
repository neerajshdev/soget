package com.njsh.myapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.njsh.downloader.DBHelper
import com.njsh.myapp.ui.components.Drawer
import com.njsh.myapp.ui.components.TopAppbar
import com.njsh.myapp.ui.pages.Navigator
import com.njsh.myapp.ui.pages.YoutubePage
import com.njsh.myapp.ui.pages.pageMap
import com.njsh.myapp.ui.theme.MyappTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity()
{
    private val TAG = javaClass.name
    private val youtubePage = YoutubePage()

    private val topAppbarComp = TopAppbar()
    private val pageList = pageMap.keys.toList()
    private val drawer = Drawer(pageList, pageList[0])

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            MyappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val scaffoldState = rememberScaffoldState()
                    val scope = rememberCoroutineScope()

                    // events handling
                    topAppbarComp.onMenuClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }

                    drawer.onDrawerClose = {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    }

                    drawer.onSelectionChange = { selection ->
                        val page = pageMap[selection]
                        if(page != null)
                        {
                            Navigator.replace(page)
                            topAppbarComp.title.value = page.tag
                        }
                        drawer.onDrawerClose?.invoke()
                    }

                    Scaffold(topBar = { topAppbarComp.Compose() },
                        drawerContent = { drawer.Compose() },
                        scaffoldState = scaffoldState,
                        content = {
                            // compose the initial page
                            Navigator.Compose()
                        })
                }
            }
        }
    }
}