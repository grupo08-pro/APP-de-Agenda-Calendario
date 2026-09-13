package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = Slate950,
    primaryContainer = IndigoPrimaryContainerDark,
    onPrimaryContainer = IndigoPrimaryContainerLight,
    secondary = BlueSecondaryDark,
    onSecondary = Slate950,
    secondaryContainer = BlueSecondaryContainerDark,
    onSecondaryContainer = BlueSecondaryContainerLight,
    tertiary = EmeraldTertiaryDark,
    onTertiary = Slate950,
    tertiaryContainer = EmeraldContainerDark,
    onTertiaryContainer = EmeraldContainerLight,
    background = Slate900,
    onBackground = Slate50,
    surface = Slate800,
    onSurface = Slate50,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate200,
    outline = Slate600
  )

private val LightColorScheme =
  lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoPrimaryContainerLight,
    onPrimaryContainer = IndigoPrimaryContainerDark,
    secondary = BlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = BlueSecondaryContainerLight,
    onSecondaryContainer = BlueSecondaryContainerDark,
    tertiary = EmeraldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldContainerLight,
    onTertiaryContainer = EmeraldContainerDark,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate300
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our refined custom brand palette
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

