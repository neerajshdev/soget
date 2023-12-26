package com.gd.reelssaver.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.ui.theme.AppTheme


@Preview
@Composable
fun TabItemPreview() {
    AppTheme {
        Surface {
            TabItem(
                modifier = Modifier.fillMaxWidth(),
                title = "Some page",
                url = "www.somesite.com",
                onRemoveTab = {},
                isSelected = true)
        }
    }
}

@Composable
fun TabItem(
    modifier: Modifier = Modifier,
    title: String,
    url: String,
    isSelected: Boolean,
    onRemoveTab: () -> Unit
) {
    Row(modifier) {
        Box(
            modifier = Modifier
                .padding(end = 10.dp, start = 12.dp)
                .width(80.dp)
                .height(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.Public,
                contentDescription = null,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!isSelected) {
            IconButton(onClick = onRemoveTab) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "RemoveTab",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}