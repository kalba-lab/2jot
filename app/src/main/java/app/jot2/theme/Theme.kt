package app.jot2.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val themeColors = listOf(
    Color(0xFF1976D2), // Blue
    Color(0xFF388E3C), // Green
    Color(0xFF7B1FA2), // Purple
    Color(0xFFF57C00), // Orange
    Color(0xFFD32F2F), // Red
    Color(0xFF00796B), // Teal
    Color(0xFFC2185B), // Pink
    Color(0xFF303F9F), // Indigo
    Color(0xFF9E9E9E), // Gray
    Color(0xFFFFFFFF)  // White
)

@Composable
fun Jot2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val primaryColor = themeColors.getOrElse(colorIndex) { themeColors[0] }
    val isWhite = primaryColor == Color(0xFFFFFFFF)

    val surfaceVariantColor = if (darkTheme) {
        Color(
            red = primaryColor.red * 0.15f + 0.12f * 0.85f,
            green = primaryColor.green * 0.15f + 0.12f * 0.85f,
            blue = primaryColor.blue * 0.15f + 0.12f * 0.85f,
            alpha = 1f
        )
    } else {
        if (isWhite) {
            Color(0xFFF5F5F5)
        } else {
            Color(
                red = primaryColor.red * 0.12f + 1f * 0.88f,
                green = primaryColor.green * 0.12f + 1f * 0.88f,
                blue = primaryColor.blue * 0.12f + 1f * 0.88f,
                alpha = 1f
            )
        }
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            secondary = primaryColor,
            onSecondary = Color.White,
            background = Color(0xFF121212),
            onBackground = Color.White,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            surfaceVariant = surfaceVariantColor,
            onSurfaceVariant = Color(0xFFE0E0E0)
        )
    } else {
        lightColorScheme(
            primary = if (isWhite) Color(0xFF1976D2) else primaryColor,
            onPrimary = Color.White,
            secondary = primaryColor,
            onSecondary = Color.Black,
            background = Color(0xFFFAFAFA),
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            surfaceVariant = surfaceVariantColor,
            onSurfaceVariant = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}