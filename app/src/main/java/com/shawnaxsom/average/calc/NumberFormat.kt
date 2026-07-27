package com.shawnaxsom.average.calc

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Formatting helpers. Grouping and the decimal separator are applied by hand
 * rather than through [java.text.DecimalFormat] so the output is identical on
 * every device locale — a calculator that shows "6,33" for two thirds of
 * nineteen would be read as six thousand three hundred and thirty three.
 */
object NumberFormat {

    /** Decimals kept on derived values such as the average. */
    const val RESULT_DECIMALS = 4

    /**
     * Rounds to at most [maxDecimals] places, drops trailing zeros and groups
     * the integer part in threes. 18/3 renders as "6", 19/3 as "6.3333".
     */
    fun format(value: BigDecimal, maxDecimals: Int = RESULT_DECIMALS): String {
        val rounded = value.setScale(maxDecimals, RoundingMode.HALF_UP).stripTrailingZeros()
        val plain = rounded.toPlainString()
        val negative = plain.startsWith("-")
        val unsigned = if (negative) plain.substring(1) else plain
        val whole = unsigned.substringBefore('.')
        val fraction = unsigned.substringAfter('.', "")
        val grouped = group(whole)
        return buildString {
            if (negative) append('-')
            append(grouped)
            if (fraction.isNotEmpty()) {
                append('.')
                append(fraction)
            }
        }
    }

    /** Formats exactly what the user typed, keeping a trailing "4." intact. */
    fun formatInput(input: String): String {
        if (input.isEmpty()) return ""
        val whole = input.substringBefore('.')
        val grouped = group(whole)
        return if (input.contains('.')) "$grouped.${input.substringAfter('.')}" else grouped
    }

    private fun group(digits: String): String {
        if (digits.length <= 3) return digits
        return buildString {
            digits.forEachIndexed { index, char ->
                if (index > 0 && (digits.length - index) % 3 == 0) append(',')
                append(char)
            }
        }
    }
}
