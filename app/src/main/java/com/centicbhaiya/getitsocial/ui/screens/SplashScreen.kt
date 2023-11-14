package com.centicbhaiya.getitsocial.ui.screens


import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.centicbhaiya.getitsocial.App
import com.centicbhaiya.getitsocial.R
import com.centicbhaiya.getitsocial.ads.AppOpenAdManager
import com.centicbhaiya.getitsocial.ads.InterstitialAdManager
import com.centicbhaiya.getitsocial.ui.theme.AppTheme
import com.centicbhaiya.getitsocial.ui.theme.useDarkTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Preview
@Composable
fun SplashScreenPreviewLight() {
    useDarkTheme = true
    AppTheme {
        SplashScreen()
    }
}


@Composable
fun SplashScreen(navController: NavController? = null) {
    val localContext = LocalContext.current

    LaunchedEffect(Unit) {
        val syncResult = syncFirebase()
        if (syncResult == FirebaseSyncResult.SucessAndUpdate || syncResult == FirebaseSyncResult.Success) {

            InterstitialAdManager.init(localContext as Activity)
            AppOpenAdManager(localContext)

            navController?.popBackStack()
//            navController?.navigate(RouteName.VideoDownloader)
        } else {
            App.toast("Something went wrong!")
            delay(2000)
            navController?.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "Logo"
            )

//            // Application name and smile emoji at the bottom
//            Text(
//                text = stringResource(R.string.app_name) + " 🙂",
//                modifier = Modifier
//                    .padding(bottom = 24.dp), // Adjust padding as needed
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.headlineMedium,
//                color = MaterialTheme.colorScheme.onBackground
//            )
        }
    }
}


enum class FirebaseSyncResult {
    Success,
    Failed,
    SucessAndUpdate // success and fetched new values
}

suspend fun syncFirebase(): FirebaseSyncResult {
    return suspendCoroutine { cont ->
        Firebase.remoteConfig
            .fetchAndActivate()
            .addOnCompleteListener() { task ->
                when {
                    task.isSuccessful && task.result -> cont.resume(FirebaseSyncResult.SucessAndUpdate)
                    task.isSuccessful -> cont.resume(FirebaseSyncResult.Success)
                    else -> cont.resume(FirebaseSyncResult.Failed)
                }
            }
    }
}

