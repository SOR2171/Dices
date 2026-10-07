package io.github.sor2171.dices

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.sor2171.dices.data.ScreenDestination
import io.github.sor2171.dices.ui.screens.MainScreen
import io.github.sor2171.dices.ui.screens.ManageDiceScreen
import io.github.sor2171.dices.ui.screens.StatisticalInformationScreen

@Composable
fun App() {
    var diceRefresh by remember { mutableStateOf(true) }
    var currentDestination by remember { mutableStateOf(ScreenDestination.Home) }
    val mainScreenLazyGridState = rememberLazyGridState()
    val diceManagerLazyListState = rememberLazyListState()

    val changeDiceRefresh = { diceRefresh = !diceRefresh }

    MaterialTheme {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                ScreenDestination.entries.forEach { destination ->
                    item(
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (currentDestination) {
                    ScreenDestination.Home -> MainScreen(
                        diceRefresh = diceRefresh,
                        changeDiceRefresh = changeDiceRefresh,
                        lazyGridState = mainScreenLazyGridState,
                        screenToSI = { currentDestination = ScreenDestination.Information },
                        screenToMD = { currentDestination = ScreenDestination.Manage }
                    )

                    ScreenDestination.Information -> StatisticalInformationScreen()

                    ScreenDestination.Manage -> ManageDiceScreen(
                        lazyColumnState = diceManagerLazyListState,
                        changeDiceRefresh = changeDiceRefresh,
                        backToMainScreen = { currentDestination = ScreenDestination.Home }
                    )
                }
            }
        }
    }
}
