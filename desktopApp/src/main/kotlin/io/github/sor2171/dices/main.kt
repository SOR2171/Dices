package io.github.sor2171.dices

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.sor2171.dices.ui.DiceApp
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(width = 1120.dp, height = 820.dp)

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Dices · 液态玻璃投骰器",
        resizable = true,
    ) {
        LaunchedEffect(Unit) {
            window.minimumSize = Dimension(860, 620)
        }
        DiceApp()
    }
}
