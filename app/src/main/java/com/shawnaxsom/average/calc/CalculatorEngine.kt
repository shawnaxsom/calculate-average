package com.shawnaxsom.average.calc

import java.math.BigDecimal

/**
 * Pure state transitions for the keypad. Every function takes the current
 * [CalculatorState] and returns the next one, which keeps the UI layer free of
 * calculator logic and makes all of this straightforward to unit test.
 */
object CalculatorEngine {

    /** Guard rail so a stuck key can't build an unbounded number. */
    const val MAX_DIGITS = 12

    fun digit(state: CalculatorState, digit: Char): CalculatorState {
        require(digit in '0'..'9') { "Not a digit: $digit" }
        if (digitCount(state.input) >= MAX_DIGITS) return state
        // A lone leading zero is replaced rather than appended, so 0 then 5 is 5.
        val input = if (state.input == "0") digit.toString() else state.input + digit
        return autoCommit(state.copy(input = input))
    }

    /** The "00" key, which behaves exactly like pressing 0 twice. */
    fun doubleZero(state: CalculatorState): CalculatorState = digit(digit(state, '0'), '0')

    /**
     * Decimal point.
     *
     * In the auto-committing modes a number is gone by the time you realise it
     * needed a fraction, so pressing "." on an empty input pulls the last
     * committed number back for editing. That is what makes "4." reachable in
     * 1-digit mode. Once an input holds a decimal point it stops auto-committing
     * and waits for OK.
     */
    fun dot(state: CalculatorState): CalculatorState {
        if (state.input.contains('.')) return state
        if (state.input.isNotEmpty()) {
            return state.copy(input = state.input + ".", inputEscaped = true)
        }
        val last = state.numbers.lastOrNull()
            ?: return state.copy(input = "0.", inputEscaped = true)
        val text = last.stripTrailingZeros().toPlainString()
        return state.copy(
            numbers = state.numbers.dropLast(1),
            input = if (text.contains('.')) text else "$text.",
            inputEscaped = true,
        )
    }

    /** OK key: commits whatever is pending. */
    fun submit(state: CalculatorState): CalculatorState =
        if (state.input.isEmpty()) state else commit(state)

    /** Backspace: trims the pending input first, then removes committed numbers. */
    fun backspace(state: CalculatorState): CalculatorState = when {
        state.input.isNotEmpty() -> {
            val input = state.input.dropLast(1)
            state.copy(input = input, inputEscaped = state.inputEscaped && input.isNotEmpty())
        }
        state.numbers.isNotEmpty() -> state.copy(numbers = state.numbers.dropLast(1))
        else -> state
    }

    /** C key: wipes the tape but keeps the display settings. */
    fun clearAll(state: CalculatorState): CalculatorState =
        CalculatorState(mode = state.mode, resultKind = state.resultKind)

    /**
     * Switches the headline figure between the average and the sum. Entry is
     * untouched: the tape, the pending input and the digit mode all stand.
     */
    fun setResultKind(state: CalculatorState, kind: ResultKind): CalculatorState =
        state.copy(resultKind = kind)

    fun removeAt(state: CalculatorState, index: Int): CalculatorState {
        if (index !in state.numbers.indices) return state
        return state.copy(numbers = state.numbers.filterIndexed { i, _ -> i != index })
    }

    /**
     * Switching to a narrower mode commits anything already long enough for it,
     * so the pending input never sits above the new digit limit.
     */
    fun setMode(state: CalculatorState, mode: EntryMode): CalculatorState =
        autoCommit(state.copy(mode = mode))

    private fun autoCommit(state: CalculatorState): CalculatorState =
        if (!state.inputEscaped &&
            state.input.isNotEmpty() &&
            digitCount(state.input) >= state.mode.digits
        ) {
            commit(state)
        } else {
            state
        }

    private fun commit(state: CalculatorState): CalculatorState {
        val value = parse(state.input)
            ?: return state.copy(input = "", inputEscaped = false)
        return state.copy(
            numbers = state.numbers + value,
            input = "",
            inputEscaped = false,
        )
    }

    private fun parse(text: String): BigDecimal? = when {
        text.isEmpty() || text == "." -> null
        text.endsWith(".") -> BigDecimal(text.dropLast(1))
        text.startsWith(".") -> BigDecimal("0$text")
        else -> BigDecimal(text)
    }

    private fun digitCount(text: String): Int = text.count { it in '0'..'9' }
}
