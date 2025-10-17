package com.github.sor2171.dices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.data.DiceDataCollection
import com.github.sor2171.dices.ui.component.AddDiceFloatingActionButton
import com.github.sor2171.dices.ui.component.DiceManagerCard
import com.github.sor2171.dices.ui.component.DiceTypeCreateDialog
import com.github.sor2171.dices.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDiceScreen(
    lazyColumnState: LazyListState,
    changeDiceRefresh: () -> Unit,
    backToMainScreen: () -> Unit
) {
    var diceExistRefresh by remember { mutableStateOf(true) }
    var diceList by remember { mutableStateOf(DiceDataCollection.diceList.toList()) }
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DiceTypeCreateDialog(
            onDismissRequest = { openDialog = false },
            onConfirmation = {
                openDialog = false
                diceList = DiceDataCollection.diceList.toList()
                diceExistRefresh = !diceExistRefresh
                changeDiceRefresh()
            },
            titleStringRes = R.string.add_dice_button,
            contentStringRes = R.string.add_dice_content,
            confirmStringRes = R.string.confirm_button,
            dismissStringRes = R.string.dismiss_button,
        )
    }
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
                                changeDiceRefresh()
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
                    onClick = { openDialog = true }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                state = lazyColumnState,
                contentPadding = PaddingValues(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(
                    items = diceList,
                    key = { diceType -> diceType.toString() + diceExistRefresh }
                ) { diceType ->
                    DiceManagerCard(
                        {
                            DiceDataCollection.deleteType(diceType.max)
                            diceList = diceList.filter { it != diceType }
                            diceExistRefresh = !diceExistRefresh
                        },
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
            rememberLazyListState(),
            {},
            {}
        )
    }
}

@Preview
@Composable
fun DiceTypeCreateDialogPreview() {
    DiceTypeCreateDialog(
        {},
        {},
        R.string.add_dice_button,
        R.string.add_dice_content,
        R.string.confirm_button,
        R.string.dismiss_button
    )
}