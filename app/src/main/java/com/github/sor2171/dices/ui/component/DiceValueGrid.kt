package com.github.sor2171.dices.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.data.DiceData
import com.github.sor2171.dices.data.DiceDataCollection

@Composable
fun DiceValueGrid(
    diceRefresh: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(
            items = diceInformationList(),
            key = { data -> data.toString() + diceRefresh }
        ) { data ->
            DiceValueCard(data)
        }

    }
}

fun diceInformationList(): MutableList<DiceData> {
    val list = mutableListOf<DiceData>()
    for (diceType in DiceDataCollection.diceList) {
        for (i in diceType.diceValue.indices) {
            list += DiceData(
                i,
                "D${diceType.max}",
                diceType.diceValue[i]
            )
        }
    }
    return list
}