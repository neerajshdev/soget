package com.njsh.reelssaver.layer.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.primary,
    desTextColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = backgroundColor)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .wrapContentSize()
                .size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Column {
            Text(text = text, color = textColor, fontSize = 14.sp)
            Text(text = desText, color = desTextColor, fontSize = 11.sp, textAlign = TextAlign.Justify)
        }
    }
}

@Preview
@Composable
fun PBigButtonLayer() {
    AppTheme {
        BigButtonLayer(
            icon = painterResource(id = R.drawable.ic_outlined_instagram),
            text = "Instagram",
            desText = "Paste link & download instagram reels"
        )
    }
}