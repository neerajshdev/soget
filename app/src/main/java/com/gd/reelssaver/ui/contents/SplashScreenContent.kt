package com.gd.reelssaver.ui.contents

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.gd.reelssaver.App
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.AppOpenAdManager
import com.gd.reelssaver.ads.InterstitialAdManager
import com.gd.reelssaver.ui.blocs.FakeSplashScreenComponent
import com.gd.reelssaver.ui.blocs.SplashScreenComponent
import com.gd.reelssaver.ui.theme.AppTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Preview
@Composable
private fun SplashScreenContentPreview() {
    AppTheme {
        SplashScreenContent(component = FakeSplashScreenComponent())
    }
}

@Composable
fun SplashScreenContent(component: SplashScreenComponent) {
    val localContext = LocalContext.current

    LaunchedEffect(Unit) {
        val syncResult = syncFirebase()
        if (syncResult == FirebaseSyncResult.SuccessAndUpdate || syncResult == FirebaseSyncResult.Success) {
            InterstitialAdManager.init(localContext as Activity)
            AppOpenAdManager(localContext)

            // show first interstitial ad on app open
            InterstitialAdManager.showAd().join()
        } else {
            App.toast("Something went wrong!")
            delay(2000)
        }

        component.finishSplash()
    }

    Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val logo = createRef()
            Logo(modifier = Modifier.constrainAs(logo) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent)
            })

            val progressIndicator = createRef()
            LinearProgressIndicator(modifier = Modifier.constrainAs(progressIndicator) {
                centerHorizontallyTo(parent)
                bottom.linkTo(parent.bottom, 100.dp)
            })
        }
    }
}

@Composable
private fun Logo(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_launcher_playstore),
        contentDescription = "Logo",
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)
    )
}

enum class FirebaseSyncResult {
    Success,
    Failed,
    SuccessAndUpdate // success and fetched new values
}

suspend fun syncFirebase(): FirebaseSyncResult {
    return suspendCoroutine { cont ->
        Firebase.remoteConfig
            .fetchAndActivate()
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful && task.result == true -> cont.resume(FirebaseSyncResult.SuccessAndUpdate)
                    task.isSuccessful -> cont.resume(FirebaseSyncResult.Success)
                    else -> cont.resume(FirebaseSyncResult.Failed)
                }
            }
    }
}