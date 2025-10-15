package com.github.sor2171.dices

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.data.DiceDataCollection
import com.github.sor2171.dices.ui.component.AddDiceFloatingActionButton
import com.github.sor2171.dices.ui.component.DiceManagerCard
import com.github.sor2171.dices.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDiceScreen(
    diceRefresh: Boolean,
    lazyColumnState: LazyListState,
    changeDiceRefresh: () -> Unit,
    backToMainScreen: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.MDButton_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                changeDiceRefresh
                                backToMainScreen()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = stringResource(R.string.back_button)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                AddDiceFloatingActionButton(
                    extended = lazyColumnState.isScrollInProgress,
                    textID = R.string.add_dice_button,
                    onClick = {
                        /*TODO*/
                        changeDiceRefresh()
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                state = lazyColumnState,
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(
                    items = DiceDataCollection.diceList
                ) { diceType ->
                    DiceManagerCard(
                        diceType
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun ManageDiceScreenPreview() {
    AppTheme(dynamicColor = false) {
        ManageDiceScreen(
            true,
            rememberLazyListState(),
            {},
            {}
        )
    }
}