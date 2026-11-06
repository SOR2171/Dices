package io.github.sor2171.dices.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sor2171.dices.core.DiceSession
import io.github.sor2171.dices.core.DieResult
import io.github.sor2171.dices.ui.component.IvoryDie
import io.github.sor2171.dices.ui.component.GlassSurface
import io.github.sor2171.dices.ui.component.Panel
import io.github.sor2171.dices.ui.component.TableFelt
import io.github.sor2171.dices.ui.theme.Palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun RollScreen(session: DiceSession, onManage: () -> Unit, onRoll: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 950.dp
        val compact = maxWidth < 600.dp
        Column(Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(top = if (compact) 2.dp else 10.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("骰子", color = Palette.Text, fontFamily = FontFamily.Serif,
                                fontSize = if (compact) 46.sp else 66.sp, letterSpacing = 14.sp, lineHeight = 80.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.width(28.dp).height(1.dp).background(Palette.Gold))
                                Text("混 合 投 掷", color = Palette.Gold, fontSize = 13.sp, letterSpacing = 2.sp)
                            }
                        }
                        if (!compact) {
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(session.formula.ifEmpty { "尚未配置骰组" }, color = Palette.Text, fontFamily = FontFamily.Serif, fontSize = 22.sp)
                                Text("${session.totalDice} 颗骰子 · 独立随机投掷", color = Palette.SecondaryText, fontSize = 12.sp)
                            }
                        }
                    }
                }
                item {
                    if (wide) {
                        Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                            DiceTable(session, onManage, onRoll, !compact, Modifier.weight(1f))
                            ResultLedger(session, Modifier.width(290.dp))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            DiceTable(session, onManage, onRoll, !compact, Modifier.fillMaxWidth())
                            ResultLedger(session, Modifier.fillMaxWidth())
                        }
                    }
                }
                item { RecentRolls(session) }
                item {
                    Text("配置与记录保留于本次打开期间。", color = Palette.SecondaryText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
            if (compact) {
                RollButton(session, onRoll, Modifier.fillMaxWidth().padding(top = 10.dp))
            }
        }
    }
}

@Composable
private fun DiceTable(session: DiceSession, onManage: () -> Unit, onRoll: () -> Unit, showRollButton: Boolean, modifier: Modifier) {
    GlassSurface(modifier) {
        Column(Modifier.padding(7.dp)) {
        Box(Modifier.fillMaxWidth()) {
            TableFelt(Modifier.matchParentSize())
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("在桌骰组", color = Palette.Text, fontSize = 17.sp, letterSpacing = 3.sp, fontFamily = FontFamily.Serif)
                        Text(session.formula.ifEmpty { "空骰组" }, color = Palette.SecondaryText, fontSize = 12.sp)
                    }
                    TextButton(onClick = onManage, enabled = !session.isRolling) {
                        Icon(Icons.Outlined.Tune, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("编辑骰组", fontSize = 12.sp)
                    }
                }
                if (session.dice.isEmpty()) {
                    Column(Modifier.fillMaxWidth().padding(vertical = 68.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Casino, null, Modifier.size(48.dp), tint = Palette.Gold)
                        Spacer(Modifier.height(16.dp))
                        Text("请先添加骰子", color = Palette.Text, fontFamily = FontFamily.Serif, fontSize = 20.sp)
                        TextButton(onClick = onManage) { Text("前往骰组配置") }
                    }
                } else {
                    FlowRow(
                        Modifier.fillMaxWidth().heightIn(min = 220.dp).padding(vertical = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        session.dice.forEachIndexed { index, die ->
                            key(die.id) { TableDie(die, index, session.isRolling) }
                        }
                    }
                }
                HorizontalDivider(color = Palette.Gold.copy(alpha = 0.18f))
                Row(
                    Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${session.totalDice} 颗在桌", color = Palette.SecondaryText, fontSize = 11.sp)
                    Text(if (session.isRolling) "投掷中" else "等待下一掷", color = Palette.Gold, fontSize = 11.sp, letterSpacing = 2.sp)
                }
            }
        }
        if (showRollButton) {
            RollButton(session, onRoll, Modifier.fillMaxWidth().padding(top = 7.dp))
        }
        }
    }
}

@Composable
private fun RollButton(session: DiceSession, onRoll: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onRoll,
        enabled = !session.isRolling && session.totalDice > 0,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = .16f), contentColor = Palette.Text),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = .42f)),
        modifier = modifier.height(58.dp),
    ) {
        Icon(Icons.Outlined.Casino, null, Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(if (session.isRolling) "正在投掷…" else "掷出骰子", fontFamily = FontFamily.Serif, fontSize = 19.sp, letterSpacing = 4.sp)
    }
}

@Composable
private fun TableDie(die: DieResult, index: Int, rolling: Boolean) {
    var shownValue by remember { mutableIntStateOf(die.value) }
    val motion = remember { Animatable(0f) }
    LaunchedEffect(rolling, die.value) {
        if (rolling) {
            motion.snapTo(0f)
            launch { motion.animateTo(1f, tween(580, easing = FastOutSlowInEasing)) }
            while (isActive) {
                shownValue = Random.nextInt(1, die.sides + 1)
                delay(70)
            }
        } else {
            motion.snapTo(0f)
            shownValue = die.value
        }
    }
    val restingAngle = when (index % 3) {
        0 -> -11f
        1 -> 9f
        else -> -4f
    }
    Column(
        Modifier.width(150.dp).semantics {
            contentDescription = "第${index + 1}颗 D${die.sides}，${if (die.value == 0) "尚未投掷" else "点数${die.value}"}"
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(150.dp).graphicsLayer {
                rotationZ = restingAngle + if (rolling) motion.value * 360f else 0f
                translationY = if (rolling) -sin(motion.value * PI).toFloat() * 24.dp.toPx() else 0f
            },
            contentAlignment = Alignment.Center,
        ) {
            IvoryDie(die.sides, shownValue, Modifier.fillMaxSize())
            if (die.sides != 6 || shownValue == 0) {
                Text(
                    if (shownValue == 0) "—" else shownValue.toString(),
                    color = Color(0xFF302A1D),
                    fontSize = if (shownValue >= 100) 24.sp else 32.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer {
                        translationX = if (die.sides == 6) -12.dp.toPx() else 0f
                        translationY = if (die.sides == 6) 10.dp.toPx() else -3.dp.toPx()
                    },
                )
            }
        }
        Text("D${die.sides}", color = Palette.Text, fontFamily = FontFamily.Serif, fontSize = 15.sp, letterSpacing = 2.sp)
        Text((index + 1).toString().padStart(2, '0'), color = Palette.Gold.copy(alpha = .75f), fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun ResultLedger(session: DiceSession, modifier: Modifier) {
    Panel(modifier) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(5.dp).background(Palette.Gold, RoundedCornerShape(1.dp)))
            Spacer(Modifier.width(10.dp))
            Text("本 次 结 果", color = Palette.Gold, fontSize = 12.sp, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            if (session.isRolling) "…" else session.lastRoll?.total?.toString() ?: "—",
            color = Palette.Text, fontFamily = FontFamily.Serif,
            fontSize = 92.sp, lineHeight = 106.sp,
        )
        Text("总点数", color = Palette.SecondaryText, fontSize = 12.sp, letterSpacing = 4.sp)
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Palette.Line)
        Spacer(Modifier.height(20.dp))
        DetailRow("骰组", session.formula.ifEmpty { "—" })
        Spacer(Modifier.height(14.dp))
        DetailRow("数量", "${session.totalDice} 颗")
        Spacer(Modifier.height(14.dp))
        val maximum = session.groups.sumOf { it.sides * it.count }
        DetailRow("范围", if (session.totalDice == 0) "—" else "${session.totalDice} — $maximum")
        Spacer(Modifier.height(24.dp))
        val result = session.lastRoll
        Text(
            when {
                session.isRolling -> "骰子尚未落定"
                result == null -> "等待首次投掷"
                else -> result.dice.joinToString(" + ") { it.value.toString() } + " = ${result.total}"
            },
            color = Palette.Gold, fontFamily = FontFamily.Serif, fontSize = 16.sp, lineHeight = 26.sp,
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(label, color = Palette.SecondaryText, fontSize = 12.sp)
        Text(value, Modifier.weight(1f), color = Palette.Text, fontSize = 13.sp, lineHeight = 20.sp)
    }
}

@Composable
private fun RecentRolls(session: DiceSession) {
    Panel(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("最近投掷", color = Palette.Text, fontFamily = FontFamily.Serif, fontSize = 17.sp, letterSpacing = 2.sp)
            Text("本次打开期间", color = Palette.SecondaryText, fontSize = 11.sp)
        }
        Spacer(Modifier.height(14.dp))
        if (session.history.isEmpty()) {
            Text("完成投掷后，结果会记录在这里。", color = Palette.SecondaryText, fontSize = 13.sp)
        } else {
            session.history.take(3).forEachIndexed { index, record ->
                if (index > 0) HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Palette.Line.copy(alpha = .45f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(record.id.toString().padStart(3, '0'), color = Palette.Gold, fontSize = 11.sp)
                    Text(record.formula, Modifier.weight(1f), color = Palette.SecondaryText, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(record.total.toString(), color = Palette.Text, fontSize = 22.sp, fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}
