package com.gd.reelssaver.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.gd.reelssaver.R
import com.gd.reelssaver.ui.theme.AppTheme

@Preview
@Composable
fun SplashPreview() {
    AppTheme {
        SplashContent(splashComponent = remember{ FakeSplashComponent() })
    }
}

@Composable
fun SplashContent(splashComponent: SplashComponent) {
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
fun Logo(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_launcher_playstore),
        contentDescription = "Logo",
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)
    )
}