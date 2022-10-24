package com.njsh.instadl.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njsh.instadl.ui.components.NativeAdView
import com.njsh.instadl.ui.theme.AppTheme


@Composable
fun ExitDialog(
    onYes: () -> Unit, onNo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .wrapContentSize()
            .shadow(elevation = 4.dp)
            .padding(16.dp)
    ) {
        Text(text = "Do you wanna exit?", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        NativeAdView()
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onYes, modifier = Modifier.weight(1f)) {
                Text(text = "YES")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onNo, modifier = Modifier.weight(1f)) {
                Text(text = "NO")
            }
        }
    }
}

@Preview
@Composable
fun PrevExitDialog() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            ExitDialog({}, {})
        }
    }
}