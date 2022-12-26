package com.njsh.reelssaver.layer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njsh.reelssaver.R
import com.njsh.reelssaver.layer.ui.theme.AppTheme


@Composable
fun BigButtonLayer(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    desText: String,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .clickable(onClick = { onClick?.invoke() })
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.surface)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .wrapContentSize()
                .size(24.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = desText,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Preview
@Composable
fun PBigButtonLayer() {
    AppTheme {
        BigButtonLayer(icon = painterResource(id = R.drawable.ic_outlined_instagram),
            text = "Instagram",
            desText = "Paste link & download instagram reels",
            onClick = {})
    }
}