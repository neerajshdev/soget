package com.gd.reelssaver.ui.screens.splash

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import com.gd.reelssaver.App
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.AppOpenAdManager
import com.gd.reelssaver.ads.InterstitialAdManager
import com.gd.reelssaver.ui.theme.AppTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Preview
@Composable
fun SplashPreview() {
    AppTheme {
        SplashContent(component = remember { FakeSplashComponent() })
    }
}

@Composable
fun SplashContent(component: SplashComponent, modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    var showTitle by remember { mutableStateOf(false) }

    val progressAlpha by animateFloatAsState(
        targetValue = if (showTitle) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "progress alpha"
    )
    
    // Setup full screen display
    val view = LocalView.current
    val primaryColor = MaterialTheme.colorScheme.surfaceContainerLowest
    if (!view.isInEditMode) {
        SideEffect {
            val window = (localContext as Activity).window
            window.statusBarColor = primaryColor.toArgb()
            window.navigationBarColor = primaryColor.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    // Ensure we restore the system UI when navigating away
    DisposableEffect(Unit) {
        onDispose {
            val window = (localContext as? Activity)?.window
            if (window != null) {
                // Restore system UI to default state when leaving splash screen
                WindowCompat.setDecorFitsSystemWindows(window, true)
            }
        }
    }

    Surface(
        color = primaryColor,
        modifier = modifier.fillMaxSize()
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize().padding(24.dp),
        ) {
            val (contentColumn, linearProgressBar) = createRefs()
            
            // Content column contains both logo and title for better alignment
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.constrainAs(contentColumn) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) {
                // Animated logo
                AnimatedAppLogo(
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                // Title with animation
                AnimatedVisibility(
                    visible = showTitle,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                    )
                ) {
                    AppTitle()
                }
            }
            
            // Progress indicator (keep at bottom of screen)
            Box(
                modifier = Modifier
                    .constrainAs(linearProgressBar) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .alpha(progressAlpha)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        // Start showing title after a short delay for sequence animation
        delay(400)
        showTitle = true
        
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

        component.onEvent(Event.Finish)
    }
}

@Composable
private fun AnimatedAppLogo(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInBounce),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .scale(scale),
        )
    }
}

@Composable
private fun AppTitle(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 48.sp,
                letterSpacing = (-1).sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Video Downloader",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
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