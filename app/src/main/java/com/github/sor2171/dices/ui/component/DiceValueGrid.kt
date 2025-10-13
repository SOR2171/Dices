package com.github.sor2171.dices.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.data.DiceDataCollection

@Composable
fun DiceValueGrid(
    key: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
    ) {
        items(
            items = DiceDataCollection.diceList,
            key = {diceType -> diceType.name + key }
        ) { diceType ->
            for (value in diceType.diceValue) {
                DiceValueCard(value, diceType.name)
            }
        }
    }
}