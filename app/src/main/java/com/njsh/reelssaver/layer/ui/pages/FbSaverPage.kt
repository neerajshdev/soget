package com.njsh.reelssaver.layer.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.layer.ui.components.Advertisement
import com.njsh.reelssaver.layer.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FbSaverPager(navController: NavController) {
    var inputString by remember { mutableStateOf("") }
    var isGetButtonEnabled by remember { mutableStateOf(false) }

    @Composable
    fun Inputs() {
        Column {
            TextField(
                value = inputString,
                onValueChange = { inputString = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row {
                OutlinedButton(onClick = {  }, modifier = Modifier.weight(1f)) {
                    Text(text = "PASTE")
                }

                Spacer(modifier = Modifier.width(16.dp))

                FilledTonalButton(
                    onClick = { /*TODO*/ },
                    enabled = isGetButtonEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "GET")
                }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Text(
                text = "Facebook",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f)
            )
        }, navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f)
            )
        })
        Spacer(modifier = Modifier.height(14.dp))
        Inputs()
        Spacer(modifier = Modifier.weight(1f))
        Advertisement(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .wrapContentSize()
        )
    }
}



@Preview
@Composable
private fun PFbSaverPage() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(16.dp)) {
                FbSaverPager(rememberNavController())
            }
        }
    }
}
