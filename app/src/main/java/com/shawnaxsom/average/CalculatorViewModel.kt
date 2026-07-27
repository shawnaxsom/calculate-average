package com.shawnaxsom.average

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.shawnaxsom.average.calc.CalculatorEngine
import com.shawnaxsom.average.calc.CalculatorState
import com.shawnaxsom.average.calc.EntryMode

/** Holds the tape across configuration changes and delegates every key to [CalculatorEngine]. */
class CalculatorViewModel : ViewModel() {

    var state: CalculatorState by mutableStateOf(CalculatorState())
        private set

    fun onDigit(digit: Char) = update { CalculatorEngine.digit(it, digit) }

    fun onDoubleZero() = update(CalculatorEngine::doubleZero)

    fun onDot() = update(CalculatorEngine::dot)

    fun onSubmit() = update(CalculatorEngine::submit)

    fun onBackspace() = update(CalculatorEngine::backspace)

    fun onClear() = update(CalculatorEngine::clearAll)

    fun onRemove(index: Int) = update { CalculatorEngine.removeAt(it, index) }

    fun onModeChange(mode: EntryMode) = update { CalculatorEngine.setMode(it, mode) }

    private fun update(transform: (CalculatorState) -> CalculatorState) {
        state = transform(state)
    }
}
