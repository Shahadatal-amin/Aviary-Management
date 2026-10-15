package com.aviary.management.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object BrandColors {
    val GreenDeep = Color(0xFF0E5F4A)
    val GreenSoft = Color(0xFF7BB5A4)
    val Gold = Color(0xFFF3C96B)
    val Background = Color(0xFFF5F1E7)
    val Card = Color(0xFFFFFDF8)
    val Accent = Color(0xFFB8E0D2)
}

private val LightColors = lightColorScheme(
    primary = BrandColors.GreenDeep,
    secondary = BrandColors.Gold,
    tertiary = BrandColors.GreenSoft,
    background = BrandColors.Background,
    surface = BrandColors.Card,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = BrandColors.GreenDeep,
    onSurface = BrandColors.GreenDeep
)

@Composable
fun AviaryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
