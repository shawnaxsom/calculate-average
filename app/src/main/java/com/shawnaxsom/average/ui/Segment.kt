package com.shawnaxsom.average.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shawnaxsom.average.ui.theme.calculatorColors

/**
 * One pill in a segmented control. The selected segment fills with the accent
 * colour; the rest stay transparent so the track shows through.
 */
@Composable
fun Segment(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = calculatorColors
    val background by animateColorAsState(
        targetValue = if (selected) colors.accent else Color.Transparent,
        animationSpec = tween(durationMillis = 160),
        label = "segment-background",
    )
    val content by animateColorAsState(
        targetValue = if (selected) colors.card else colors.muted,
        animationSpec = tween(durationMillis = 160),
        label = "segment-content",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .selectable(selected = selected, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = content,
        )
    }
}
