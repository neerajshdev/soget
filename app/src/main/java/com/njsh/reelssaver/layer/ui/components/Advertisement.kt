package com.njsh.reelssaver.layer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.Advertisement(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) { // TODO: REPLACE WITH NATIVE AD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(bottom = 16.dp)
                .background(color = Color.Cyan)
        )
    }
}