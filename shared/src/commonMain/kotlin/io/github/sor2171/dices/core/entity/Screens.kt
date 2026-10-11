package io.github.sor2171.dices.core.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import dices.shared.generated.resources.MDButton_name
import dices.shared.generated.resources.Res
import dices.shared.generated.resources.SIButton_name
import dices.shared.generated.resources.home_screen_name
import org.jetbrains.compose.resources.StringResource

enum class Screens(
    val icon: ImageVector,
    override val label: StringResource
) : LabelResolvable {
    Home(
        label = Res.string.home_screen_name,
        icon = Icons.Default.Home
    ),
    Information(
        label = Res.string.SIButton_name,
        icon = Icons.Default.Info
    ),
    Manage(
        label = Res.string.MDButton_name,
        icon = Icons.Default.Settings
    )
}