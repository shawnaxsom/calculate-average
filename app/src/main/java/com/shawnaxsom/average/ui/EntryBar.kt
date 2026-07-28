package com.shawnaxsom.average.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shawnaxsom.average.calc.EntryMode
import com.shawnaxsom.average.calc.NumberFormat
import com.shawnaxsom.average.ui.theme.calculatorColors

/**
 * Shows what is currently being typed alongside the digit-mode selector.
 *
 * The mode decides how many keystrokes make up one number: in "1" every press
 * is its own number, in "free" you type as much as you like and press OK.
 */
@Composable
fun EntryBar(
    input: String,
    mode: EntryMode,
    onModeChange: (EntryMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = calculatorColors
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.card)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "NEXT",
                style = MaterialTheme.typography.labelSmall,
                color = colors.muted,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = NumberFormat.formatInput(input).ifEmpty { mode.placeholder },
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = if (input.isEmpty()) colors.muted.copy(alpha = 0.5f) else colors.keyLabel,
                maxLines = 1,
                softWrap = false,
            )
        }
        Spacer(Modifier.height(8.dp))
        ModeSelector(mode = mode, onModeChange = onModeChange)
    }
}

@Composable
private fun ModeSelector(mode: EntryMode, onModeChange: (EntryMode) -> Unit) {
    val colors = calculatorColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "DIGITS",
            style = MaterialTheme.typography.labelSmall,
            color = colors.muted,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp, end = 4.dp),
        )
        EntryMode.entries.forEach { option ->
            Segment(
                text = option.label,
                selected = option == mode,
                onClick = { onModeChange(option) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    }
}
