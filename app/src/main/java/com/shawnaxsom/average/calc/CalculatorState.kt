package com.shawnaxsom.average.calc

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * How many digits a number takes before it is committed automatically.
 *
 * In [ONE], every single digit you press becomes its own number, so a run of
 * key presses is summarised as you type. [FREE] never auto-commits; you press
 * OK (or the decimal point, see [CalculatorEngine.dot]) to finish a number.
 */
enum class EntryMode(val digits: Int, val label: String, val placeholder: String) {
    ONE(1, "1", "#"),
    TWO(2, "2", "##"),
    THREE(3, "3", "###"),
    FREE(Int.MAX_VALUE, "free", "#.##"),
}

/**
 * Everything the calculator needs to render itself.
 *
 * @param inputEscaped set once the pending [input] has opted out of
 * auto-committing (currently by typing a decimal point). It resets whenever the
 * input is committed or emptied.
 */
data class CalculatorState(
    val numbers: List<BigDecimal> = emptyList(),
    val input: String = "",
    val mode: EntryMode = EntryMode.ONE,
    val inputEscaped: Boolean = false,
) {
    val count: Int get() = numbers.size

    val sum: BigDecimal
        get() = numbers.fold(BigDecimal.ZERO) { acc, value -> acc + value }

    /** Null until at least one number has been entered. */
    val average: BigDecimal?
        get() = if (numbers.isEmpty()) null
        else sum.divide(BigDecimal(numbers.size), MathContext(16, RoundingMode.HALF_UP))

    val min: BigDecimal? get() = numbers.minOrNull()

    val max: BigDecimal? get() = numbers.maxOrNull()

    val isEmpty: Boolean get() = numbers.isEmpty() && input.isEmpty()
}
