package com.centicbhaiya.getitsocial

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.centicbhaiya.getitsocial.ui.components.BottomNavigationBar
import com.centicbhaiya.getitsocial.ui.components.NavBarItem
import com.centicbhaiya.getitsocial.ui.screens.TabsScreen
import com.centicbhaiya.getitsocial.ui.state.FbVideoDataState
import com.centicbhaiya.getitsocial.ui.theme.AppTheme
import online.desidev.onestate.stateManager

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        stateManager.configure {
            stateFactory(FbVideoDataState::class) { FbVideoDataState(emptyList()) }
        }

        setContent {
            AppTheme {
                val navController = rememberNavController()
                Scaffold(bottomBar = {
                    BottomNavigationBar(
                        NavBarItem(
                            name = "Tab",
                            iconRes = R.drawable.ic_tabs,
                            onItemSelect = { navController.navigate("tabs")}
                        ),

                        NavBarItem(
                            name = "Progress",
                            iconRes = R.drawable.round_download_24,
                            onItemSelect = { navController.navigate("progress"); Log.d(
                                TAG,
                                "navgate to progress"
                            ) }
                        ),

                        NavBarItem(
                            name = "Player",
                            iconRes = R.drawable.round_folder_24,
                            onItemSelect = { navController.navigate("player") }
                        )
                    )
                }) { innerPaddings ->
                    NavHost(navController = navController, "tabs", modifier = Modifier.padding(innerPaddings)) {
                        composable("tabs") {
                            TabsScreen()
                        }

                        composable("progress") {
                            Text(text = "Progress")
                        }

                        composable("player") {
                            Text(text = "Player")
                        }
                    }
                }
            }
        }
    }
}




