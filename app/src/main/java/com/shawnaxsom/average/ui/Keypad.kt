package com.shawnaxsom.average.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shawnaxsom.average.ui.theme.calculatorColors

/** Every key the pad can emit. */
sealed interface Key {
    data class Digit(val value: Char) : Key
    data object DoubleZero : Key
    data object Dot : Key
    data object Submit : Key
    data object Backspace : Key
    data object Clear : Key
}

private const val KEY_ROWS = 4
private val KeyRowHeight = 64.dp
private val KeyGap = 8.dp

/**
 * Three columns of digits with the actions stacked down the right, so OK stays
 * under the thumb and spans the height of two ordinary keys.
 */
@Composable
fun Keypad(onKey: (Key) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(KeyRowHeight * KEY_ROWS + KeyGap * (KEY_ROWS - 1)),
        horizontalArrangement = Arrangement.spacedBy(KeyGap),
    ) {
        Column(
            modifier = Modifier.weight(3f),
            verticalArrangement = Arrangement.spacedBy(KeyGap),
        ) {
            DigitRow("789", onKey)
            DigitRow("456", onKey)
            DigitRow("123", onKey)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(KeyGap),
            ) {
                KeyButton(
                    label = "0",
                    onClick = { onKey(Key.Digit('0')) },
                    modifier = Modifier.weight(1f),
                )
                KeyButton(
                    label = "00",
                    onClick = { onKey(Key.DoubleZero) },
                    modifier = Modifier.weight(1f),
                    tone = KeyTone.Muted,
                )
                KeyButton(
                    label = ".",
                    onClick = { onKey(Key.Dot) },
                    modifier = Modifier.weight(1f),
                    tone = KeyTone.Muted,
                    description = "Decimal point",
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(KeyGap),
        ) {
            KeyButton(
                label = "⌫",
                onClick = { onKey(Key.Backspace) },
                modifier = Modifier.weight(1f),
                tone = KeyTone.Muted,
                description = "Delete",
            )
            KeyButton(
                label = "C",
                onClick = { onKey(Key.Clear) },
                modifier = Modifier.weight(1f),
                tone = KeyTone.Warning,
                description = "Clear all",
            )
            KeyButton(
                label = "OK",
                onClick = { onKey(Key.Submit) },
                modifier = Modifier.weight(2f),
                tone = KeyTone.Accent,
                description = "Add number",
            )
        }
    }
}

@Composable
private fun ColumnScope.DigitRow(digits: String, onKey: (Key) -> Unit) {
    Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(KeyGap),
    ) {
        digits.forEach { digit ->
            KeyButton(
                label = digit.toString(),
                onClick = { onKey(Key.Digit(digit)) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private enum class KeyTone { Neutral, Muted, Accent, Warning }

@Composable
private fun KeyButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: KeyTone = KeyTone.Neutral,
    description: String? = null,
) {
    val colors = calculatorColors
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(),
        label = "key-scale",
    )

    val background: Color = when (tone) {
        KeyTone.Neutral -> colors.key
        KeyTone.Muted -> colors.key.copy(alpha = 0.55f)
        KeyTone.Accent -> colors.accent
        KeyTone.Warning -> colors.highlight.copy(alpha = 0.16f)
    }
    val labelColor: Color = when (tone) {
        KeyTone.Neutral, KeyTone.Muted -> colors.keyLabel
        KeyTone.Accent -> colors.card
        KeyTone.Warning -> colors.highlight
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .clickable(interactionSource = interactionSource, indication = null) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .semantics { if (description != null) contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = if (label.length > 1) 21.sp else 27.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
        )
    }
}
