package io.github.sor2171.dices.ui.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dices.shared.generated.resources.RFAButton_name
import dices.shared.generated.resources.Res
import io.github.sor2171.dices.core.utils.DiceDataCollection
import io.github.sor2171.dices.ui.component.DiceValueGrid
import io.github.sor2171.dices.ui.component.RollFloatingActionButton

@Composable
fun MainScreen(
    diceRefresh: Boolean,
    changeDiceRefresh: () -> Unit,
    lazyGridState: LazyGridState,
) {
    Scaffold(
        floatingActionButton = {
            RollFloatingActionButton(
                extended = lazyGridState.isScrollInProgress,
                textID = Res.string.RFAButton_name,
                onClick = {
                    changeDiceRefresh()
                    DiceDataCollection.rollAll()
                }
            )
        }
    ) { paddingValues ->
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                DiceValueGrid(
                    diceRefresh = diceRefresh,
                    lazyGridState = lazyGridState,
                )
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(lazyGridState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            true,
            {},
            rememberLazyGridState(),
        )
    }
}