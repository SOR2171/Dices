package io.github.sor2171.dices.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sor2171.dices.core.DiceGroup
import io.github.sor2171.dices.core.DiceSession
import io.github.sor2171.dices.ui.component.Panel
import io.github.sor2171.dices.ui.theme.Palette

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiceLibraryScreen(session: DiceSession) {
    var customSides by remember { mutableStateOf("") }
    var customAttempted by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val parsedSides = customSides.toIntOrNull()
    val invalidSides = customAttempted && (parsedSides == null || parsedSides !in 2..DiceSession.MAX_SIDES)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("骰 组", color = Palette.Gold, fontSize = 11.sp, letterSpacing = 2.sp)
                Text("骰组配置", color = Palette.Text, fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                Text("选择面数，调整数量，组合下一次投掷。", color = Palette.SecondaryText, fontSize = 14.sp)
            }
        }
        item {
            Panel(modifier = Modifier.fillMaxWidth()) {
                Text("快速添加", color = Palette.Text, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Spacer(Modifier.height(6.dp))
                Text("点选面数，每次添加一颗。", color = Palette.SecondaryText, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    listOf(4, 6, 8, 10, 12, 20, 100).forEach { sides ->
                        val count = session.groups.firstOrNull { it.sides == sides }?.count ?: 0
                        FilterChip(
                            selected = count > 0,
                            enabled = !session.isRolling && count < DiceSession.MAX_COUNT &&
                                session.totalDice < DiceSession.MAX_DICE,
                            onClick = {
                                message = if (session.addType(sides)) null else "当前组合已达到数量上限。"
                            },
                            label = { Text(if (count > 0) "D$sides · $count" else "D$sides") },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "每种最多 ${DiceSession.MAX_COUNT} 颗，合计最多 ${DiceSession.MAX_DICE} 颗。",
                    color = Palette.SecondaryText,
                    fontSize = 12.sp,
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("当前组合", color = Palette.Text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("${session.totalDice} 颗骰子", color = Palette.SecondaryText, fontSize = 13.sp)
            }
            message?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
        }
        if (session.groups.isEmpty()) {
            item {
                Panel(modifier = Modifier.fillMaxWidth()) {
                    Text("从第一颗骰子开始", color = Palette.Text, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text("选择上方的常用面数，或在下方创建自定义骰子。", color = Palette.SecondaryText, fontSize = 14.sp)
                }
            }
        }
        items(session.groups, key = { it.sides }) { group ->
            DiceGroupEditor(
                group = group,
                enabled = !session.isRolling,
                canAdd = group.count < DiceSession.MAX_COUNT && session.totalDice < DiceSession.MAX_DICE,
                onCountChange = { count ->
                    message = if (session.setCount(group.sides, count)) null else "当前组合已达到数量上限。"
                },
            )
        }
        item {
            Panel(modifier = Modifier.fillMaxWidth()) {
                Text("自定义面数", color = Palette.Text, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("自定义 2–${DiceSession.MAX_SIDES} 面骰子。已有面数会增加一颗。", color = Palette.SecondaryText, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = customSides,
                    onValueChange = {
                        customSides = it
                        customAttempted = false
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !session.isRolling,
                    label = { Text("骰子面数") },
                    placeholder = { Text("例如 24") },
                    prefix = { Text("D") },
                    singleLine = true,
                    isError = invalidSides,
                    supportingText = {
                        Text(if (invalidSides) "请输入 2–${DiceSession.MAX_SIDES} 之间的整数。" else "投掷结果从 1 到你设置的面数。")
                    },
                    shape = RoundedCornerShape(12.dp),
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    enabled = !session.isRolling,
                    onClick = {
                        customAttempted = true
                        if (parsedSides != null && parsedSides in 2..DiceSession.MAX_SIDES) {
                            if (session.addType(parsedSides)) {
                                customSides = ""
                                customAttempted = false
                                message = null
                            } else {
                                message = "当前组合已达到数量上限，请先减少一些骰子。"
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("添加骰子")
                }
                message?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        }
        item {
            Text("调整组合后，回到投掷台开始新的一轮。", color = Palette.SecondaryText, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DiceGroupEditor(
    group: DiceGroup,
    enabled: Boolean,
    canAdd: Boolean,
    onCountChange: (Int) -> Unit,
) {
    Panel(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Palette.Selected,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("D${group.sides}", color = Palette.Text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("${group.sides} 面骰子", color = Palette.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("单颗结果 1–${group.sides}", color = Palette.SecondaryText, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(color = Palette.Background, shape = RoundedCornerShape(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onCountChange(group.count - 1) }, enabled = enabled) {
                        Icon(Icons.Outlined.Remove, contentDescription = "减少 D${group.sides} 数量", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        group.count.toString(),
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center,
                        color = Palette.Text,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(onClick = { onCountChange(group.count + 1) }, enabled = enabled && canAdd) {
                        Icon(Icons.Outlined.Add, contentDescription = "增加 D${group.sides} 数量", modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { onCountChange(0) }, enabled = enabled) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "移除 D${group.sides}", tint = Palette.SecondaryText)
            }
        }
    }
}
