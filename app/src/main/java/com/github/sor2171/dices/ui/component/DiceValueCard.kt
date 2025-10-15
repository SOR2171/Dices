package com.github.sor2171.dices.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.data.DiceData

@Composable
fun DiceValueCard(
    data: DiceData
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                text = data.diceType,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Text(
                text = "${data.value}",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "No.${data.id}",
                textAlign = TextAlign.Right,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun DiceValueCardPreview() {
    DiceValueCard(
        DiceData(1,"D6", 3)
    )
}