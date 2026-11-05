package io.github.sor2171.dices.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sor2171.dices.core.entity.DiceData
import io.github.sor2171.dices.core.utils.DiceDataCollection
import io.github.sor2171.dices.core.entity.DiceRollRecord
import io.github.sor2171.dices.ui.component.DiceValueCard

@Composable
fun StatisticalInformationScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        val history = DiceDataCollection.rollHistory
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无记录", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history.reversed()) { record ->
                    RollRecordCard(record)
                }
            }
        }
    }
}

@Composable
fun RollRecordCard(record: DiceRollRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val expressionText =expression(record.diceResults)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "掷骰结果 (${record.diceResults.size} 个骰子)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "总和: $expressionText=${record.totalSum}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(record.diceResults) { dice ->
                    DiceValueCard(data = dice)
                }
                    }
                }
            }
        }
fun expression(dices: List<DiceData>): String {
    val counts = dices.groupBy { it.diceType }
    val parts = counts.map { (type, list) ->
        "${list.size}$type"
    }
    return parts.joinToString("+")
}