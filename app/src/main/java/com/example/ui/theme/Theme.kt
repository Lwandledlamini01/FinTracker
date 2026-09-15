package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF000000),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFE5E5EA),
  onPrimaryContainer = Color(0xFF000000),
  secondary = AccentPurple,
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFF2F2F7),
  onSecondaryContainer = Color(0xFF000000),
  background = BgLight,
  onBackground = Color(0xFF000000),
  surface = CardLight,
  onSurface = Color(0xFF000000),
  surfaceVariant = Color(0xFFF2F2F7),
  onSurfaceVariant = TextSecondary,
  outline = Color(0xFFE5E5EA),
  outlineVariant = Color(0x0A000000)
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFFFFFFF),
  onPrimary = Color(0xFF000000),
  primaryContainer = Color(0xFF2C2C2E),
  onPrimaryContainer = Color(0xFFFFFFFF),
  secondary = AccentPurple,
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFF1C1C1E),
  onSecondaryContainer = Color(0xFFFFFFFF),
  background = BgDark,
  onBackground = Color(0xFFFFFFFF),
  surface = CardDark,
  onSurface = Color(0xFFFFFFFF),
  surfaceVariant = Color(0xFF1C1C1E),
  onSurfaceVariant = TextSecondary,
  outline = Color(0xFF38383A),
  outlineVariant = Color(0x14FFFFFF)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
          isAppearanceLightStatusBars = !darkTheme
          isAppearanceLightNavigationBars = !darkTheme
        }
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
