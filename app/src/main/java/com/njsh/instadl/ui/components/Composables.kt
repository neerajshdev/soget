package com.njsh.instadl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njsh.instadl.R
import com.njsh.instadl.ui.theme.AppTheme

@Composable
fun LeftCurvedButton(
    modifier: Modifier = Modifier, painter: Painter, label: String, onClick: () -> Unit
) {
    val colors = MaterialTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .clip(
                shape = RoundedCornerShape(
                    topStart = 100.dp, bottomStart = 100.dp
                )
            )
            .clickable(onClick = onClick)
            .background(color = colors.primary)
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Icon(
            painter = painter, contentDescription = null, tint = colors.onPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = colors.onPrimary)
    }
}

@Preview
@Composable
fun PrevLeftCurvedButton() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()) {
                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_instagram),
                    label = "INSTAGRAM",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {})
            }
        }
    }
}


@Composable
fun RightCurvedHeading(modifier: Modifier = Modifier, label: String) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
            .background(color = MaterialTheme.colors.primary)
            .padding(vertical = 16.dp, horizontal = 24.dp), contentAlignment = Alignment.CenterEnd
    ) {
        Text(text = label, color = MaterialTheme.colors.onPrimary)
    }
}


@Preview
@Composable
fun PrevRightCurvedHeading() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            RightCurvedHeading(modifier = Modifier.padding(16.dp), "ALL VIDEO DOWNLOADER")
        }
    }
}


@Composable
fun CircularProgressBar(isLoading: State<Boolean>) {
    if (isLoading.value) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    }
}