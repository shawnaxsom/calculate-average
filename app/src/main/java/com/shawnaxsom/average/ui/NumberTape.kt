package com.shawnaxsom.average.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shawnaxsom.average.calc.NumberFormat
import com.shawnaxsom.average.ui.theme.calculatorColors
import java.math.BigDecimal

/**
 * The running tape of entered numbers. Each chip carries a delete badge, so a
 * mistyped value can be pulled out without clearing everything.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberTape(
    numbers: List<BigDecimal>,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = calculatorColors
    val scrollState = rememberScrollState()

    // Keep the newest chips in view as the tape grows past one row.
    LaunchedEffect(numbers.size, scrollState.maxValue) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(modifier = modifier, contentAlignment = Alignment.BottomStart) {
        if (numbers.isEmpty()) {
            Text(
                text = "Tap numbers below — the average updates as you go.",
                style = MaterialTheme.typography.labelLarge,
                color = colors.muted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Bottom,
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    numbers.forEachIndexed { index, value ->
                        NumberChip(
                            value = value,
                            isLatest = index == numbers.lastIndex,
                            onRemove = { onRemove(index) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberChip(
    value: BigDecimal,
    isLatest: Boolean,
    onRemove: () -> Unit,
) {
    val colors = calculatorColors
    val text = NumberFormat.format(value)
    // The newest chip sits at full strength; older ones recede a little.
    val emphasis by animateFloatAsState(
        targetValue = if (isLatest) 1f else 0.78f,
        animationSpec = tween(durationMillis = 200),
        label = "chip-emphasis",
    )

    Box(contentAlignment = Alignment.TopEnd) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.accent.copy(alpha = 0.10f + 0.12f * emphasis))
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accent,
                modifier = Modifier.alpha(emphasis),
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colors.keyLabel.copy(alpha = 0.85f))
                .clickable(onClick = onRemove)
                .semantics { contentDescription = "Remove $text" },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "×",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.card,
            )
        }
    }
}
