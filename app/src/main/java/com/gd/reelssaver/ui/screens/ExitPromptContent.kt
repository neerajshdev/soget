package com.gd.reelssaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.ui.composables.MediumSizeNativeAd
import com.gd.reelssaver.ui.navigation.ExitPromptComponent
import com.gd.reelssaver.ui.navigation.ExitPromptComponent.Event

@Composable
fun ExitPromptContent(component: ExitPromptComponent) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            Text(text = "Do you really want to exit?", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            MediumSizeNativeAd {}
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                TextButton(onClick = { component.onEvent(Event.CancelExit) }) {
                    Text(text = "No")
                }

                Button(onClick = { component.onEvent(Event.ConfirmExit) }) {
                    Text(text = "Yes")
                }
            }
        }
    }
}