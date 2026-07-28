# Average

An Android calculator that averages numbers as you type them. Enter a run of
values and the count, sum, range and average update on every keystroke — no
equals key, no operator to remember.

## Average or sum

The toggle at the top of the result card decides which figure gets the big
readout. Whichever one is not on show is demoted to a stat tile, so both are
always on screen.

It is a display choice only. Digit entry is unaffected: the tape, the pending
number and the digit mode all carry across when you flip it.

## How entry works

The **DIGITS** selector under the tape decides how many keystrokes make up one
number:

| Mode | Behaviour |
| --- | --- |
| `1` | Every single digit you press becomes its own number. Typing `9 3 6` gives three numbers averaging 6. |
| `2` | Digits pair up. `1 2 3 4` gives 12 and 34. |
| `3` | Digits group in threes. `1 2 3 4 5 6` gives 123 and 456. |
| `free` | Type as long a number as you like, then press **OK**. |

### Decimals

Results are never truncated to whole numbers: `9 3 5` averages to `5.6667`, not
`5`. Derived values are computed with `BigDecimal` and shown to four decimal
places with trailing zeros trimmed.

Decimal *input* works even in the auto-committing modes. Because a single digit
is already committed by the time you reach for the point, pressing `.` on an
empty entry pulls the last number back for editing — so in 1-digit mode
`4` `.` `5` `OK` enters 4.5. Once an entry contains a decimal point it stops
auto-committing and waits for **OK**.

### Other keys

- `⌫` trims the number being typed, then removes committed numbers one at a time.
- `C` clears the tape but keeps the selected mode.
- `×` on any chip removes just that number and recalculates.

## Building

```sh
./gradlew assembleDebug     # APK at app/build/outputs/apk/debug/
./gradlew test              # unit tests for the calculator engine
```

Requires the Android SDK (compileSdk 35) and JDK 17+. Minimum supported device
is API 26.

## Layout

The calculator logic lives in `app/src/main/java/com/shawnaxsom/average/calc/`
as pure Kotlin with no Android dependencies — `CalculatorEngine` is a set of
state-in/state-out functions, which is what the unit tests exercise. Everything
under `ui/` is Jetpack Compose and holds no arithmetic.
