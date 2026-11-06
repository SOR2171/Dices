package io.github.sor2171.dices.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sor2171.dices.core.DiceSession
import io.github.sor2171.dices.core.RollRecord
import io.github.sor2171.dices.ui.component.Panel
import io.github.sor2171.dices.ui.theme.Palette
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(session: DiceSession) {
    var confirmClear by remember { mutableStateOf(false) }
    val records = session.history
    val average = if (records.isEmpty()) "—" else
        ((records.sumOf { it.total }.toDouble() / records.size * 10).roundToInt() / 10.0).toString()
    val highest = records.maxOfOrNull { it.total }?.toString() ?: "—"

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("清空本次历史？") },
            text = { Text("将移除这次打开应用以来保存的投掷记录，投掷台上的当前结果会保留。") },
            confirmButton = {
                TextButton(
                    enabled = !session.isRolling,
                    onClick = {
                        session.clearHistory()
                        confirmClear = false
                    },
                ) { Text("清空历史") }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("保留记录") } },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("记 录", color = Palette.Gold, fontSize = 11.sp, letterSpacing = 2.sp)
                Text("投掷记录", color = Palette.Text, fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                Text("仅保存本次打开期间最近 50 次投掷。", color = Palette.SecondaryText, fontSize = 14.sp)
            }
        }
        item {
            Panel(modifier = Modifier.fillMaxWidth()) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    if (maxWidth < 340.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CompactHistoryMetric("记录数", records.size.toString())
                            CompactHistoryMetric("平均总点数", average)
                            CompactHistoryMetric("最高总点数", highest)
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HistoryMetric("记录数", records.size.toString(), Modifier.weight(1f))
                            HistoryMetric("平均总点数", average, Modifier.weight(1f))
                            HistoryMetric("最高总点数", highest, Modifier.weight(1f))
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text("统计基于下方记录；不同骰子组合的总点数会有不同范围。", color = Palette.SecondaryText, fontSize = 12.sp)
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("投掷记录", color = Palette.Text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                TextButton(
                    enabled = records.isNotEmpty() && !session.isRolling,
                    onClick = { confirmClear = true },
                ) { Text("清空记录") }
            }
        }
        if (records.isEmpty()) {
            item {
                Panel(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Surface(color = Palette.Selected, shape = RoundedCornerShape(20.dp)) {
                            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.History, contentDescription = null, tint = Palette.Gold, modifier = Modifier.size(30.dp))
                            }
                        }
                        Text("暂无投掷记录", color = Palette.Text, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("完成一次混合投掷后，结果将保存在这里。", color = Palette.SecondaryText, fontSize = 13.sp)
                    }
                }
            }
        }
        items(records, key = { it.id }) { record ->
            HistoryRecordCard(record)
        }
    }
}

@Composable
private fun CompactHistoryMetric(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Palette.SecondaryText, fontSize = 13.sp)
        Text(value, color = Palette.Text, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    }
}
@Composable
private fun HistoryMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(value, color = Palette.Text, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = Palette.SecondaryText, fontSize = 12.sp)
    }
}

@Composable
private fun HistoryRecordCard(record: RollRecord) {
    Panel(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ROLL ${record.id.toString().padStart(3, '0')}", color = Palette.SecondaryText, fontSize = 11.sp, letterSpacing = 1.sp)
                Text(
                    record.formula,
                    color = Palette.Text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(record.total.toString(), color = Palette.Gold, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Text("总点数", color = Palette.SecondaryText, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            record.dice.groupBy { it.sides }.entries.joinToString("\n") { (sides, dice) ->
                "D$sides   " + dice.joinToString(" · ") { it.value.toString() }
            },
            color = Palette.SecondaryText,
            fontSize = 13.sp,
            lineHeight = 22.sp,
        )
    }
}
