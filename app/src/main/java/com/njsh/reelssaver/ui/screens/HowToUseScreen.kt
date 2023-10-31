package com.njsh.reelssaver.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToUseScreen(navController: NavController) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        "Open Instagram or Facebook and copy the video link.",
        "Paste the video link in the 'Video Link' field.",
        "Click 'Fetch Video Data' to preview the video.",
        "Click 'Download Video' to save the video to your device."
    )

    Scaffold(topBar = {
        TopAppBar(title = { Text("How to Use ReelsSaver") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back to Main Page")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = steps[currentStep],
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                steps.indices.forEach { index ->
                    Box(modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (currentStep == index) colorScheme.primaryContainer else colorScheme.surfaceContainer,
                            CircleShape
                        )
                        .clickable { currentStep = index }
                        .padding(8.dp),
                        contentAlignment = Alignment.Center) {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (currentStep == index) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHowToUseScreen() {
    AppTheme {
        HowToUseScreen(navController = rememberNavController())
    }
}
