package com.shawnaxsom.average.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class CalculatorEngineTest {

    private fun type(state: CalculatorState, keys: String): CalculatorState =
        keys.fold(state) { acc, key ->
            when (key) {
                in '0'..'9' -> CalculatorEngine.digit(acc, key)
                '.' -> CalculatorEngine.dot(acc)
                '=' -> CalculatorEngine.submit(acc)
                '<' -> CalculatorEngine.backspace(acc)
                else -> error("Unknown key $key")
            }
        }

    private fun state(mode: EntryMode = EntryMode.ONE) = CalculatorState(mode = mode)

    private fun numbers(state: CalculatorState) = state.numbers.map { it.toPlainString() }

    @Test
    fun `one digit mode commits every keystroke`() {
        val result = type(state(), "936")

        assertEquals(listOf("9", "3", "6"), numbers(result))
        assertEquals("", result.input)
        assertEquals(3, result.count)
    }

    @Test
    fun `average of the screenshot tape is exact`() {
        val result = type(state(), "936")

        assertEquals(BigDecimal("18"), result.sum)
        assertEquals("6", NumberFormat.format(result.average!!))
    }

    @Test
    fun `average keeps decimals when the division is not whole`() {
        val result = type(state(), "935")

        assertEquals(BigDecimal("17"), result.sum)
        assertEquals("5.6667", NumberFormat.format(result.average!!))
    }

    @Test
    fun `two digit mode pairs keystrokes`() {
        val result = type(state(EntryMode.TWO), "1234567")

        assertEquals(listOf("12", "34", "56"), numbers(result))
        assertEquals("7", result.input)
    }

    @Test
    fun `three digit mode groups keystrokes in threes`() {
        val result = type(state(EntryMode.THREE), "123456")

        assertEquals(listOf("123", "456"), numbers(result))
    }

    @Test
    fun `free mode waits for ok`() {
        val result = type(state(EntryMode.FREE), "1234=56=")

        assertEquals(listOf("1234", "56"), numbers(result))
    }

    @Test
    fun `decimal point reopens the number just committed`() {
        // 4 auto-commits in 1-digit mode; "." pulls it back so 4.5 is typeable.
        val result = type(state(), "4.5")

        assertEquals("4.5", result.input)
        assertTrue(result.numbers.isEmpty())
        assertTrue(result.inputEscaped)

        val committed = type(result, "=")
        assertEquals(listOf("4.5"), numbers(committed))
    }

    @Test
    fun `decimal input suspends auto commit until ok`() {
        val result = type(state(), "1.25")

        assertEquals("1.25", result.input)
        assertTrue(result.numbers.isEmpty())
    }

    @Test
    fun `decimal point on an empty tape starts a leading zero`() {
        val result = type(state(), ".5=")

        assertEquals(listOf("0.5"), numbers(result))
    }

    @Test
    fun `second decimal point is ignored`() {
        val result = type(state(EntryMode.FREE), "1.2.5")

        assertEquals("1.25", result.input)
    }

    @Test
    fun `decimal values average with decimals`() {
        val result = type(state(), "1.5=2.5=3=")

        assertEquals(BigDecimal("7.0"), result.sum)
        assertEquals("2.3333", NumberFormat.format(result.average!!))
    }

    @Test
    fun `backspace trims the pending input before the tape`() {
        val start = type(state(EntryMode.FREE), "12=34")

        val trimmed = type(start, "<")
        assertEquals("3", trimmed.input)
        assertEquals(listOf("12"), numbers(trimmed))

        val emptied = type(trimmed, "<")
        assertEquals("", emptied.input)
        assertEquals(listOf("12"), numbers(emptied))

        val popped = type(emptied, "<")
        assertTrue(popped.numbers.isEmpty())
    }

    @Test
    fun `backspace on an empty calculator is a no-op`() {
        val result = type(state(), "<<<")

        assertTrue(result.isEmpty)
        assertNull(result.average)
    }

    @Test
    fun `clearing keeps the selected mode`() {
        val result = CalculatorEngine.clearAll(type(state(EntryMode.THREE), "123456"))

        assertTrue(result.isEmpty)
        assertEquals(EntryMode.THREE, result.mode)
    }

    @Test
    fun `removing a number recomputes the average`() {
        val start = type(state(), "936")

        val result = CalculatorEngine.removeAt(start, 1)
        assertEquals(listOf("9", "6"), numbers(result))
        assertEquals("7.5", NumberFormat.format(result.average!!))
    }

    @Test
    fun `removing an out of range index is a no-op`() {
        val start = type(state(), "936")

        assertEquals(start, CalculatorEngine.removeAt(start, 5))
        assertEquals(start, CalculatorEngine.removeAt(start, -1))
    }

    @Test
    fun `switching to a narrower mode commits the pending input`() {
        val start = type(state(EntryMode.FREE), "42")

        val result = CalculatorEngine.setMode(start, EntryMode.ONE)
        assertEquals(listOf("42"), numbers(result))
        assertEquals("", result.input)
    }

    @Test
    fun `switching to a wider mode leaves the pending input alone`() {
        val start = type(state(EntryMode.TWO), "7")

        val result = CalculatorEngine.setMode(start, EntryMode.THREE)
        assertEquals("7", result.input)
        assertTrue(result.numbers.isEmpty())
    }

    @Test
    fun `double zero key enters two zeros`() {
        val oneDigit = CalculatorEngine.doubleZero(state())
        assertEquals(listOf("0", "0"), numbers(oneDigit))

        val free = CalculatorEngine.doubleZero(type(state(EntryMode.FREE), "5"))
        assertEquals("500", free.input)
    }

    @Test
    fun `leading zero is replaced rather than appended`() {
        val result = type(state(EntryMode.FREE), "05")

        assertEquals("5", result.input)
    }

    @Test
    fun `input is capped at the digit limit`() {
        val result = type(state(EntryMode.FREE), "1".repeat(CalculatorEngine.MAX_DIGITS + 5))

        assertEquals(CalculatorEngine.MAX_DIGITS, result.input.length)
    }

    @Test
    fun `min and max track the tape`() {
        val result = type(state(), "9361")

        assertEquals(BigDecimal("1"), result.min)
        assertEquals(BigDecimal("9"), result.max)
    }

    @Test
    fun `empty tape has no average`() {
        assertNull(state().average)
        assertEquals(BigDecimal.ZERO, state().sum)
    }

    @Test
    fun `result follows the selected kind`() {
        val averaging = type(state(), "936")
        assertEquals("6", NumberFormat.format(averaging.result!!))

        val summing = CalculatorEngine.setResultKind(averaging, ResultKind.SUM)
        assertEquals("18", NumberFormat.format(summing.result!!))
    }

    @Test
    fun `toggling the result kind leaves entry untouched`() {
        val start = type(state(EntryMode.TWO), "1234567")

        val toggled = CalculatorEngine.setResultKind(start, ResultKind.SUM)

        assertEquals(start.numbers, toggled.numbers)
        assertEquals("7", toggled.input)
        assertEquals(EntryMode.TWO, toggled.mode)
        assertEquals(start.copy(resultKind = ResultKind.SUM), toggled)
    }

    @Test
    fun `digits keep grouping the same way while summing`() {
        val summing = CalculatorEngine.setResultKind(state(), ResultKind.SUM)

        val result = type(summing, "936")
        assertEquals(listOf("9", "3", "6"), numbers(result))
        assertEquals("18", NumberFormat.format(result.result!!))
    }

    @Test
    fun `sum keeps decimals`() {
        val summing = CalculatorEngine.setResultKind(state(), ResultKind.SUM)

        val result = type(summing, "1.25=0.5=")
        assertEquals("1.75", NumberFormat.format(result.result!!))
    }

    @Test
    fun `empty tape has no result in either mode`() {
        assertNull(state().result)
        assertNull(CalculatorEngine.setResultKind(state(), ResultKind.SUM).result)
    }

    @Test
    fun `clearing keeps the selected result kind`() {
        val summing = CalculatorEngine.setResultKind(type(state(), "936"), ResultKind.SUM)

        val result = CalculatorEngine.clearAll(summing)
        assertTrue(result.isEmpty)
        assertEquals(ResultKind.SUM, result.resultKind)
    }
}
