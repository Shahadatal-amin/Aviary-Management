package com.aviary.management.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object BrandColors {
    val GreenDeep = Color(0xFF0E5F4A)
    val GreenSoft = Color(0xFF7BB5A4)
    val Gold = Color(0xFFF0C76A)
    val Background = Color(0xFFF5F1E7)
    val Surface = Color(0xFFFFFDF8)
    val TextPrimary = Color(0xFF123B32)
}

private val LightColors = lightColorScheme(
    primary = BrandColors.GreenDeep,
    secondary = BrandColors.Gold,
    tertiary = BrandColors.GreenSoft,
    background = BrandColors.Background,
    surface = BrandColors.Surface,
    onPrimary = Color.White,
    onSurface = BrandColors.TextPrimary,
    onBackground = BrandColors.TextPrimary
)

@Composable
fun AviaryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
