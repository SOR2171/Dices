package io.github.sor2171.dices

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dices.shared.generated.resources.Res
import dices.shared.generated.resources.table_background
import io.github.sor2171.dices.core.DiceSession
import io.github.sor2171.dices.ui.screens.DiceLibraryScreen
import io.github.sor2171.dices.ui.screens.HistoryScreen
import io.github.sor2171.dices.ui.screens.RollScreen
import io.github.sor2171.dices.ui.theme.DicesTheme
import io.github.sor2171.dices.ui.theme.Palette
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private enum class Page(val label: String) {
    Roll("混合投掷"),
    Library("骰组配置"),
    History("投掷记录"),
}

@Composable
fun App() {
    val session = remember { DiceSession() }
    DicesApp(session)
}

@Composable
fun DicesApp(session: DiceSession) {
    var page by remember { mutableStateOf(Page.Roll) }
    val scope = rememberCoroutineScope()
    DicesTheme {
        Box(Modifier.fillMaxSize().background(Palette.Background)) {
            Image(
                painterResource(Res.drawable.table_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0f to Color(0xFF080B08).copy(alpha = 0.14f),
                        0.30f to Palette.Background.copy(alpha = 0.10f),
                        0.65f to Palette.Background.copy(alpha = 0.16f),
                        1f to Palette.Background.copy(alpha = 0.10f),
                    )
                )
            )
            Box(Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.10f), Color.Transparent, Color.Black.copy(alpha = 0.10f)))
            ))
            BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                val compact = maxWidth < 700.dp
                Column(Modifier.fillMaxSize()) {
                    Header(page, compact, onSelect = { page = it })
                    Box(
                        Modifier.weight(1f).fillMaxWidth()
                            .padding(horizontal = if (compact) 18.dp else 42.dp)
                            .padding(top = if (compact) 16.dp else 20.dp)
                    ) {
                        when (page) {
                            Page.Roll -> RollScreen(
                                session,
                                onManage = { page = Page.Library },
                                onRoll = { scope.launch { session.roll() } },
                            )
                            Page.Library -> DiceLibraryScreen(session)
                            Page.History -> HistoryScreen(session)
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = if (compact) 20.dp else 44.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("DICES", color = Palette.Gold, fontSize = 10.sp, letterSpacing = 3.sp, fontFamily = FontFamily.Serif)
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.width(20.dp).height(1.dp).background(Palette.Line))
                        Spacer(Modifier.width(12.dp))
                        Text("骰子", color = Palette.SecondaryText, fontSize = 11.sp)
                        Spacer(Modifier.weight(1f))
                        Text("1.1.1", color = Palette.SecondaryText, fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(selected: Page, compact: Boolean, onSelect: (Page) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color(0xFF0B100D).copy(alpha = 0.10f))) {
        if (compact) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) { Brand() }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Page.entries.forEach { NavigationItem(it, selected == it, onSelect) }
            }
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 42.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Brand()
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
                    Page.entries.forEach { NavigationItem(it, selected == it, onSelect) }
                }
            }
        }
        HorizontalDivider(color = Palette.Gold.copy(alpha = 0.28f))
    }
}

@Composable
private fun Brand() {
    Icon(Icons.Outlined.Casino, null, Modifier.size(28.dp), tint = Palette.Gold)
    Spacer(Modifier.width(12.dp))
    Text("DICES", fontSize = 26.sp, letterSpacing = 5.sp, fontFamily = FontFamily.Serif, color = Palette.Text)
    Spacer(Modifier.width(16.dp))
    Text("骰子", fontSize = 13.sp, letterSpacing = 4.sp, color = Palette.Gold)
}

@Composable
private fun NavigationItem(page: Page, selected: Boolean, onSelect: (Page) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = { onSelect(page) }) {
            Text(
                page.label,
                color = if (selected) Palette.Text else Palette.SecondaryText,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                letterSpacing = 2.sp,
            )
        }
        Box(Modifier.width(24.dp).height(2.dp).background(if (selected) Palette.Gold else Color.Transparent))
    }
}
