package com.njsh.reelssaver.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njsh.reelssaver.ui.components.NativeAdView
import com.njsh.reelssaver.ui.theme.AppTheme


@Composable
fun ExitDialog(
    onYes: () -> Unit, onNo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .wrapContentSize()
            .shadow(elevation = 2.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Do you wanna exit?",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        NativeAdView()
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onYes, modifier = Modifier.weight(1f)) {
                Text(text = "YES")
            }
            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = onNo, modifier = Modifier.weight(1f)) {
                Text(text = "CANCEL")
            }
        }
    }
}


@Preview
@Composable
fun PreviewExitScreen() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            ExitDialog({}, {})
        }
    }
}