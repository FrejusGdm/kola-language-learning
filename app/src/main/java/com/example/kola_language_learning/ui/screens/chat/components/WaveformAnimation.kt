package com.example.kola_language_learning.ui.screens.chat.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.kola_language_learning.ui.theme.MintGreen
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaveformAnimation(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    Canvas(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
    ) {
        val width = size.width
        val height = size.height
        val points = 100
        val amplitude = if (isRecording) height * 0.3f else height * 0.1f

        for (i in 0..points) {
            val x = width * i / points
            val y = height / 2 + amplitude * sin(
                phase + 6f * PI.toFloat() * i / points
            ).toFloat()

            drawCircle(
                color = MintGreen,
                radius = 2.dp.toPx(),
                center = Offset(x, y),
                alpha = if (isRecording) 0.8f else 0.4f
            )
        }
    }
}