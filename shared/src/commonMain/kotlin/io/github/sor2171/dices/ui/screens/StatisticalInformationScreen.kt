package io.github.sor2171.dices.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sor2171.dices.core.entity.DiceData
import io.github.sor2171.dices.ui.component.DiceValueCard


@Composable
fun StatisticalInformationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "历史掷骰记录",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(testHistory) { record ->
                HistoryCard(record)
            }
        }
    }
}

@Composable
fun HistoryCard(record: TestRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.id,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "总和: ${record.total}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (dice in record.dices) {
                    Box(modifier = Modifier.width(100.dp)) {
                        DiceValueCard(data = dice)
                    }
                }
            }
        }
    }
}data class TestRecord(
    val id: String,
    val total: Int,
    val dices: List<DiceData>
)

val testHistory = listOf(
    TestRecord(
        id = "第 1 次掷骰",
        total = 75,
        dices = listOf(
            DiceData(id = 1, diceType = "D6", value = 6),
            DiceData(id = 2, diceType = "D6", value = 4),
            DiceData(id = 3, diceType = "D6", value = 5),
            DiceData(id = 4, diceType = "D6", value = 5),
            DiceData(id = 5, diceType = "D6", value = 5),
            DiceData(id = 6, diceType = "D6", value = 5),
            DiceData(id = 7, diceType = "D6", value = 5),
            DiceData(id = 8, diceType = "D6", value = 5),
            DiceData(id = 9, diceType = "D6", value = 5),
            DiceData(id = 10, diceType = "D6", value = 5),
            DiceData(id = 11, diceType = "D6", value = 5),
            DiceData(id = 12, diceType = "D6", value = 5),
            DiceData(id = 13, diceType = "D6", value = 5),
            DiceData(id = 14, diceType = "D6", value = 5),
            DiceData(id = 15, diceType = "D6", value = 5),

        )
    ),
    TestRecord(
        id = "第 2 次掷骰",
        total = 28,
        dices = listOf(
            DiceData(id = 1, diceType = "D21", value = 20),
            DiceData(id = 2, diceType = "D2", value = 1),
            DiceData(id = 3, diceType = "D24", value = 2),
            DiceData(id = 4, diceType = "D29", value = 5)
        )
    )
)
