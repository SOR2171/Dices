package com.github.sor2171.dices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.data.DiceDataCollection
import com.github.sor2171.dices.ui.component.DiceValueGrid
import com.github.sor2171.dices.ui.component.RollFloatingActionButton
import com.github.sor2171.dices.ui.component.ScreenJumpButton
import com.github.sor2171.dices.ui.theme.AppTheme

@Composable
fun MainScreen(
    diceRefresh: Boolean,
    changeDiceRefresh: () -> Unit,
    lazyGridState: LazyGridState,
    screenToSI: () -> Unit,
    screenToMD: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            RollFloatingActionButton(
                extended = lazyGridState.isScrollInProgress,
                textID = R.string.RFAButton_name,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScreenJumpButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(vertical = 12.dp),
                    textID = R.string.SIButton_name,
                    jumpScreen = screenToSI
                )

                ScreenJumpButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(vertical = 12.dp),
                    textID = R.string.MDButton_name,
                    jumpScreen = screenToMD
                )

                DiceValueGrid(
                    diceRefresh = diceRefresh
                )
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    AppTheme(dynamicColor = false) {
        MainScreen(
            true,
            {},
            rememberLazyGridState(),
            {},
            {}
        )
    }
}