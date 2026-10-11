package io.github.sor2171.dices

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import io.github.sor2171.dices.core.entity.Screens
import io.github.sor2171.dices.ui.screens.MainScreen
import io.github.sor2171.dices.ui.screens.ManageDiceScreen
import io.github.sor2171.dices.ui.screens.StatisticalInformationScreen

@Composable
fun App() {
    var diceRefresh by remember { mutableStateOf(true) }
    var currentDestination by remember { mutableStateOf(Screens.Home) }
    val mainScreenLazyGridState = rememberLazyGridState()
    val diceManagerLazyListState = rememberLazyListState()

    val changeDiceRefresh = { diceRefresh = !diceRefresh }

    MaterialTheme {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                Screens.entries.forEach { destination ->
                    item(
                        modifier = Modifier.padding(vertical = 8.dp),
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label()
                            )
                        },
                        label = { Text(destination.label()) },
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
                    Screens.Home -> MainScreen(
                        diceRefresh = diceRefresh,
                        changeDiceRefresh = changeDiceRefresh,
                        lazyGridState = mainScreenLazyGridState,
                    )

                    Screens.Information -> StatisticalInformationScreen()

                    Screens.Manage -> ManageDiceScreen(
                        lazyColumnState = diceManagerLazyListState,
                        changeDiceRefresh = changeDiceRefresh,
                    )
                }
            }
        }
    }
}
