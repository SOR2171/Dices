package io.github.sor2171.dices

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import javax.imageio.ImageIO
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

fun main() = application {
    val icon = remember {
        checkNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("dices.png")).use {
            ImageIO.read(it)
        }
    }
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1440.dp, height = 960.dp),
        title = "DICES · 骰子",
    ) {
        SideEffect {
            window.minimumSize = Dimension(420, 620)
            window.iconImage = icon
        }
        App()
    }
}
