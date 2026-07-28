package com.shawnaxsom.average.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shawnaxsom.average.CalculatorViewModel
import com.shawnaxsom.average.calc.CalculatorState
import com.shawnaxsom.average.calc.EntryMode
import com.shawnaxsom.average.calc.NumberFormat
import com.shawnaxsom.average.calc.ResultKind
import com.shawnaxsom.average.ui.theme.AverageTheme
import com.shawnaxsom.average.ui.theme.calculatorColors

@Composable
fun AverageScreen(viewModel: CalculatorViewModel = viewModel()) {
    AverageScreen(
        state = viewModel.state,
        onKey = { key ->
            when (key) {
                is Key.Digit -> viewModel.onDigit(key.value)
                Key.DoubleZero -> viewModel.onDoubleZero()
                Key.Dot -> viewModel.onDot()
                Key.Submit -> viewModel.onSubmit()
                Key.Backspace -> viewModel.onBackspace()
                Key.Clear -> viewModel.onClear()
            }
        },
        onRemove = viewModel::onRemove,
        onModeChange = viewModel::onModeChange,
        onResultKindChange = viewModel::onResultKindChange,
    )
}

@Composable
fun AverageScreen(
    state: CalculatorState,
    onKey: (Key) -> Unit,
    onRemove: (Int) -> Unit,
    onModeChange: (EntryMode) -> Unit,
    onResultKindChange: (ResultKind) -> Unit,
) {
    val colors = calculatorColors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colors.backdropTop, colors.backdropBottom))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            ResultCard(
                state = state,
                onResultKindChange = onResultKindChange,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(14.dp))
            Box(modifier = Modifier.weight(1f)) {
                NumberTape(
                    numbers = state.numbers,
                    onRemove = onRemove,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(Modifier.height(10.dp))
            EntryBar(
                input = state.input,
                mode = state.mode,
                onModeChange = onModeChange,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Keypad(onKey = onKey, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ResultCard(
    state: CalculatorState,
    onResultKindChange: (ResultKind) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = calculatorColors
    val result = state.result
    // Whichever total is not on show gets demoted to a stat tile, so both are
    // always visible and the toggle only decides which one is big.
    val (secondaryLabel, secondaryValue) = when (state.resultKind) {
        ResultKind.AVERAGE -> "SUM" to state.average?.let { NumberFormat.format(state.sum) }
        ResultKind.SUM -> "AVERAGE" to state.average?.let { NumberFormat.format(it) }
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = colors.card,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ResultToggle(
                selected = state.resultKind,
                onSelect = onResultKindChange,
            )
            Spacer(Modifier.height(6.dp))
            AnimatedContent(
                targetState = result?.let { NumberFormat.format(it) } ?: "—",
                transitionSpec = {
                    fadeIn(tween(140)) togetherWith fadeOut(tween(140))
                },
                label = "result",
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.displayLarge,
                    color = if (result == null) colors.muted else colors.accent,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatTile("COUNT", state.count.toString(), Modifier.weight(1f))
                StatTile(secondaryLabel, secondaryValue ?: "—", Modifier.weight(1f))
                StatTile(
                    label = "RANGE",
                    value = state.min?.let { min ->
                        val max = state.max ?: min
                        if (min == max) NumberFormat.format(min)
                        else "${NumberFormat.format(min)}–${NumberFormat.format(max)}"
                    } ?: "—",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Picks whether the headline figure is the average or the running total. */
@Composable
private fun ResultToggle(selected: ResultKind, onSelect: (ResultKind) -> Unit) {
    val colors = calculatorColors
    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(colors.key.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ResultKind.entries.forEach { option ->
            Segment(
                text = option.label,
                selected = option == selected,
                onClick = { onSelect(option) },
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = calculatorColors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.muted,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = colors.highlight,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 880)
@Composable
private fun AverageScreenPreview() {
    AverageTheme {
        AverageScreen(
            state = CalculatorState(
                numbers = listOf(
                    java.math.BigDecimal("9"),
                    java.math.BigDecimal("3"),
                    java.math.BigDecimal("5"),
                ),
                input = "4.",
                mode = EntryMode.ONE,
                inputEscaped = true,
            ),
            onKey = {},
            onRemove = {},
            onModeChange = {},
            onResultKindChange = {},
        )
    }
}
