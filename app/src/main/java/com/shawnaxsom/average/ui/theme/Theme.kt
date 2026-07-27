package com.shawnaxsom.average.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The handful of colours Material 3 has no slot for: the keypad surfaces and
 * the backdrop gradient.
 */
data class CalculatorColors(
    val backdropTop: Color,
    val backdropBottom: Color,
    val card: Color,
    val key: Color,
    val keyLabel: Color,
    val accent: Color,
    val accentPressed: Color,
    val highlight: Color,
    val muted: Color,
)

private val DarkExtras = CalculatorColors(
    backdropTop = InkBase,
    backdropBottom = InkDeep,
    card = InkRaised,
    key = InkKey,
    keyLabel = TextDark,
    accent = TealDark,
    accentPressed = TealDarkPressed,
    highlight = AmberDark,
    muted = TextMutedDark,
)

private val LightExtras = CalculatorColors(
    backdropTop = PaperBase,
    backdropBottom = PaperDeep,
    card = PaperRaised,
    key = PaperKey,
    keyLabel = TextLight,
    accent = TealLight,
    accentPressed = TealLightPressed,
    highlight = AmberLight,
    muted = TextMutedLight,
)

private val DarkColors = darkColorScheme(
    primary = TealDark,
    onPrimary = InkDeep,
    secondary = AmberDark,
    onSecondary = InkDeep,
    background = InkBase,
    onBackground = TextDark,
    surface = InkRaised,
    onSurface = TextDark,
    onSurfaceVariant = TextMutedDark,
    outline = TextMutedDark,
)

private val LightColors = lightColorScheme(
    primary = TealLight,
    onPrimary = Color.White,
    secondary = AmberLight,
    onSecondary = Color.White,
    background = PaperBase,
    onBackground = TextLight,
    surface = PaperRaised,
    onSurface = TextLight,
    onSurfaceVariant = TextMutedLight,
    outline = TextMutedLight,
)

private val LocalCalculatorColors: ProvidableCompositionLocal<CalculatorColors> =
    staticCompositionLocalOf { DarkExtras }

/** Access to the extra palette, mirroring how `MaterialTheme.colorScheme` reads. */
val calculatorColors: CalculatorColors
    @Composable @ReadOnlyComposable get() = LocalCalculatorColors.current

private val CalculatorTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 84.sp,
        lineHeight = 88.sp,
        letterSpacing = (-2).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp,
    ),
)

@Composable
fun AverageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalCalculatorColors provides if (darkTheme) DarkExtras else LightExtras,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = CalculatorTypography,
            content = content,
        )
    }
}
