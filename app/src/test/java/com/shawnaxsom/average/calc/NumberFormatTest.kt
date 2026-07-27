package com.shawnaxsom.average.calc

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class NumberFormatTest {

    @Test
    fun `whole results lose their decimal tail`() {
        assertEquals("6", NumberFormat.format(BigDecimal("6.0000")))
        assertEquals("0", NumberFormat.format(BigDecimal.ZERO))
    }

    @Test
    fun `fractions are kept to four places`() {
        assertEquals("6.3333", NumberFormat.format(BigDecimal("6.33333333")))
        assertEquals("2.5", NumberFormat.format(BigDecimal("2.5")))
    }

    @Test
    fun `rounding is half up`() {
        assertEquals("0.6667", NumberFormat.format(BigDecimal("0.66666666")))
        assertEquals("1.0001", NumberFormat.format(BigDecimal("1.00005")))
    }

    @Test
    fun `thousands are grouped`() {
        assertEquals("1,234", NumberFormat.format(BigDecimal("1234")))
        assertEquals("1,234,567.89", NumberFormat.format(BigDecimal("1234567.89")))
        assertEquals("999", NumberFormat.format(BigDecimal("999")))
    }

    @Test
    fun `negatives keep their sign outside the grouping`() {
        assertEquals("-1,234.5", NumberFormat.format(BigDecimal("-1234.5")))
    }

    @Test
    fun `raw input keeps a trailing decimal point`() {
        assertEquals("4.", NumberFormat.formatInput("4."))
        assertEquals("12,345", NumberFormat.formatInput("12345"))
        assertEquals("", NumberFormat.formatInput(""))
    }
}
