package io.github.sor2171.dices.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenDestination(
    val label: String,
    val icon: ImageVector = Icons.Default.Home
) {
     Home("Main screen"),
     Information("Statistical information"),
     Manage("Manage dice")
}