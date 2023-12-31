package com.gd.reelssaver.ui.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withSave
import com.gd.reelssaver.ui.util.debugLine

@Preview
@Composable
fun ShimmerPreview() {
    Shimmer(modifier = Modifier.size(500.dp))
}

@Preview
@Composable
fun ShimmerPainterPreivew() {
    Surface {
        Column {
            Image(
                modifier = Modifier
                    .size(500.dp)
                    .debugLine(),
                painter = rememberShimmerPainter(),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .size(500.dp)
                    .debugLine(),
                painter = rememberShimmerPainter(),
                contentDescription = null
            )
        }

    }
}

@Composable
fun Shimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val animatedValue by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1000, easing = LinearEasing),
            RepeatMode.Restart
        ), label = "Shimmer effect"
    )

    Column(modifier) {
        repeat(5) {
            ShimmerItem(modifier = Modifier.fillMaxWidth(), animatedValue = animatedValue)
        }
    }
}

@Composable
fun ShimmerItem(modifier: Modifier, animatedValue: Float) {
    Box(modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawShimmer(animatedValue)
        }
    }
}


@Composable
fun rememberShimmerPainter(): ShimmerPainter {
    val transition = rememberInfiniteTransition(label = "shimmer transition")
    val animatedValue by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1000, easing = LinearEasing),
            RepeatMode.Restart
        ), label = "Shimmer effect"
    )

    return ShimmerPainter(animatedValue)
}

class ShimmerPainter internal constructor(val animatedValue: Float) : Painter() {
    override fun DrawScope.onDraw() {
        drawShimmer(animatedValue)
    }

    override val intrinsicSize: Size = Size.Unspecified
}


private fun DrawScope.drawShimmer(animatedValue: Float) {
    drawIntoCanvas { canvas ->
        val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint()
        val width = size.width
        val height = size.height
        val colors = intArrayOf(
            Color.Transparent.toArgb(),
            Color.White.toArgb(),
            Color.Transparent.toArgb()
        )
        val positions = floatArrayOf(
            0f,
            animatedValue,
            1f
        )
        val shader = android.graphics.LinearGradient(
            0f, 0f, width, height,
            colors, positions,
            android.graphics.Shader.TileMode.CLAMP
        )
        paint.shader = shader

        canvas.nativeCanvas.withSave {
            val rect = android.graphics.RectF(0f, 0f, width, height)
            drawRoundRect(rect, 8f, 8f, paint)
        }
    }
}