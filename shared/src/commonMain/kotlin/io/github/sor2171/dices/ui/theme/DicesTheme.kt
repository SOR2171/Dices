package io.github.sor2171.dices.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Palette {
    val Text = Color(0xFFF3E3C5)
    val Gold = Color(0xFFCBA36B)
    val Selected = Color(0xFF344335)
    val Background = Color(0xFF0B1512)
    val SecondaryText = Color(0xFFB7B09F)
    val Line = Color(0xFF6B5736)
    val Panel = Color(0xFF12251F)
    val Paper = Color(0xFFE5D4AC)
}

@Composable
fun DicesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Palette.Gold,
            onPrimary = Palette.Background,
            primaryContainer = Palette.Selected,
            onPrimaryContainer = Palette.Text,
            secondary = Palette.Gold,
            secondaryContainer = Palette.Selected,
            onSecondaryContainer = Palette.Text,
            background = Palette.Background,
            onBackground = Palette.Text,
            surface = Palette.Panel,
            onSurface = Palette.Text,
            onSurfaceVariant = Palette.SecondaryText,
            surfaceVariant = Palette.Selected,
            surfaceContainer = Palette.Panel,
            surfaceContainerLow = Palette.Panel,
            outline = Palette.Gold,
            outlineVariant = Palette.Line,
            error = Color(0xFFFFA89B),
        ),
        typography = Typography(
            headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 42.sp, fontWeight = FontWeight.Medium),
            headlineMedium = TextStyle(fontSize = 26.sp, lineHeight = 36.sp, fontWeight = FontWeight.Medium),
            titleLarge = TextStyle(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium),
            titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
            bodyLarge = TextStyle(fontSize = 15.sp, lineHeight = 25.sp),
            bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 23.sp),
            bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 19.sp),
            labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            labelSmall = TextStyle(fontSize = 11.sp, letterSpacing = 1.sp),
        ),
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp),
        ),
        content = content,
    )
}
