package io.github.sor2171.dices.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dices.shared.generated.resources.MDButton_name
import dices.shared.generated.resources.Res
import dices.shared.generated.resources.add_dice_button
import dices.shared.generated.resources.add_dice_content
import dices.shared.generated.resources.back_button
import dices.shared.generated.resources.confirm_button
import dices.shared.generated.resources.dismiss_button
import io.github.sor2171.dices.data.DiceDataCollection
import io.github.sor2171.dices.ui.component.AddDiceFloatingActionButton
import io.github.sor2171.dices.ui.component.DiceManagerCard
import io.github.sor2171.dices.ui.component.DiceTypeCreateDialog
import org.jetbrains.compose.resources.stringResource

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
            titleStringRes = Res.string.add_dice_button,
            contentStringRes = Res.string.add_dice_content,
            confirmStringRes = Res.string.confirm_button,
            dismissStringRes = Res.string.dismiss_button,
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
                            text = stringResource(Res.string.MDButton_name),
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
                                contentDescription = stringResource(Res.string.back_button)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                AddDiceFloatingActionButton(
                    extended = lazyColumnState.isScrollInProgress,
                    textID = Res.string.add_dice_button,
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
    MaterialTheme {
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
        Res.string.add_dice_button,
        Res.string.add_dice_content,
        Res.string.confirm_button,
        Res.string.dismiss_button
    )
}