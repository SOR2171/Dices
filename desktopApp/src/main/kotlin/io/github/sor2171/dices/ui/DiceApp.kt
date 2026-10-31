package io.github.sor2171.dices.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sor2171.dices.core.RollParser
import io.github.sor2171.dices.core.RollResult
import io.github.sor2171.dices.core.Roller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val AccentCyan = Color(0xFF7BE7F5)
private val AccentPink = Color(0xFFFF84C8)
private val Gold = Color(0xFFFFD97A)
private val ErrorRed = Color(0xFFFF9C9C)
private val TextPrimary = Color(0xFFF6FAFF)
private val TextSecondary = Color.White.copy(alpha = 0.68f)
private val TextTertiary = Color.White.copy(alpha = 0.52f)

private val ScrimColor = Color(0x800B0B14)

/** 竖版壁纸裁切时偏上取景，露出发色和亮部 */
private const val WallpaperBias = 0.30f

private val TitleShadow = Shadow(color = Color.Black.copy(alpha = 0.55f), offset = Offset(0f, 3f), blurRadius = 22f)

/** 统一的小文本组件，避免依赖 material3 */
@Composable
private fun Txt(
    text: String,
    color: Color,
    size: TextUnit,
    weight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    shadow: Shadow? = null,
    align: TextAlign? = null,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = size,
            fontWeight = weight,
            letterSpacing = letterSpacing,
            shadow = shadow,
            textAlign = align ?: TextAlign.Unspecified,
        ),
        maxLines = maxLines,
    )
}

/** 所有界面状态，一个输入框 + 一个按钮就靠它统一调度 */
class DiceUiState(initialWallpaper: ImageBitmap? = null, initialName: String = "默认壁纸") {
    var input by mutableStateOf("")
    var expression by mutableStateOf("")
    var results by mutableStateOf<List<RollResult>>(emptyList())
    var history by mutableStateOf<List<String>>(emptyList())
    var error by mutableStateOf<String?>(null)
    var rolling by mutableStateOf(false)
    var showHelp by mutableStateOf(false)
    var wallpaper by mutableStateOf(initialWallpaper)
    var wallpaperName by mutableStateOf(initialName)
    var lastExpression by mutableStateOf<String?>(null)
}

/**
 * 一个输入框 + 一个按钮，全部功能都从这里进：
 *   2d6 / d20 / 1d8+3 / 2d6+1d8+3 / d6 x5   -> 投骰
 *   /bg                                     -> 换背景图
 *   /bg reset                               -> 恢复默认背景
 *   /clear                                  -> 清空
 *   /help 或 ?                              -> 说明
 */
class DiceController(
    private val state: DiceUiState,
    private val scope: CoroutineScope,
) {
    fun submit() {
        if (state.rolling) return
        val text = state.input.trim()
        if (text.isEmpty()) {
            val last = state.lastExpression
            if (last == null) {
                state.error = "先写个式子，比如 2d6，再按按钮"
            } else {
                rollExpression(last)
            }
            return
        }
        handle(text)
    }

    private fun handle(text: String) {
        val lower = text.lowercase()
        when {
            text == "?" || lower == "/help" || lower == "help" -> toggleHelp()

            lower == "/clear" || lower == "/cls" || lower == "clear" || lower == "清空" -> {
                state.results = emptyList()
                state.history = emptyList()
                state.expression = ""
                state.input = ""
                state.error = null
            }

            lower.startsWith("/bg") || lower.startsWith("背景") -> handleBackground(text)

            else -> rollExpression(text)
        }
    }

    private fun handleBackground(text: String) {
        val argument = text.trim().substringAfter(' ', "").trim()
        when {
            argument.isEmpty() || argument == "pick" || argument == "换" ->
                scope.launch { pickBackground() }

            argument == "reset" || argument == "default" || argument == "默认" -> {
                val builtIn = WallpaperStore.loadBuiltIn()
                if (builtIn == null) {
                    state.error = "内置壁纸也找不到了……"
                } else {
                    state.wallpaper = builtIn
                    state.wallpaperName = "默认壁纸"
                    state.error = null
                    state.input = ""
                }
            }

            else -> {
                val file = File(argument.trim('"', '\'', ' '))
                scope.launch {
                    val bitmap = withContext(Dispatchers.IO) { WallpaperStore.load(file) }
                    if (bitmap == null) {
                        state.error = "这张图读不出来：${file.name}"
                    } else {
                        state.wallpaper = bitmap
                        state.wallpaperName = file.name
                        state.error = null
                        state.input = ""
                        WallpaperStore.savePath(file)
                    }
                }
            }
        }
    }

    fun toggleHelp() {
        state.showHelp = !state.showHelp
        state.error = null
    }

    fun rollExpression(expression: String) {
        val parsed = try {
            RollParser.parse(expression)
        } catch (exception: Exception) {
            state.error = exception.message ?: "这个式子我看不懂"
            return
        }

        state.error = null
        state.lastExpression = expression
        scope.launch {
            state.rolling = true
            repeat(9) {
                state.results = Roller.roll(parsed)
                delay(36)
            }
            val finalResults = Roller.roll(parsed)
            state.results = finalResults
            state.expression = parsed.raw.trim()
            state.rolling = false

            val summary = if (finalResults.size == 1) {
                finalResults.first().total.toString()
            } else {
                finalResults.joinToString("/") { it.total.toString() }
            }
            state.history = (listOf("${parsed.raw.trim()} → $summary") + state.history).take(12)
        }
    }

    private suspend fun pickBackground() {
        val file = WallpaperStore.chooseImage() ?: return
        val bitmap = withContext(Dispatchers.IO) { WallpaperStore.load(file) }
        if (bitmap == null) {
            state.error = "这张图读不出来：${file.name}"
            return
        }
        state.wallpaper = bitmap
        state.wallpaperName = file.name
        state.input = ""
        state.error = null
        WallpaperStore.savePath(file)
    }
}

@Composable
fun DiceApp() {
    val state = remember { DiceUiState() }
    DiceAppContent(state)
}

@Composable
fun DiceAppContent(state: DiceUiState) {
    val scope = rememberCoroutineScope()
    val controller = remember(state) { DiceController(state, scope) }
    var rootSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(Unit) {
        if (state.wallpaper == null) {
            val loaded = withContext(Dispatchers.IO) { WallpaperStore.initial() }
            if (loaded != null) {
                state.wallpaper = loaded.first
                state.wallpaperName = loaded.second
            }
        }
    }

    CompositionLocalProvider(
        LocalBackdropImage provides state.wallpaper,
        LocalBackdropSize provides rootSize,
        LocalBackdropScrim provides ScrimColor,
        LocalBackdropBias provides WallpaperBias,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(FallbackGradient)
                .onGloballyPositioned { coordinates ->
                    rootSize = Size(
                        coordinates.size.width.toFloat(),
                        coordinates.size.height.toFloat(),
                    )
                }
        ) {
            state.wallpaper?.let { wallpaper ->
                WallpaperLayer(
                    image = wallpaper,
                    modifier = Modifier.fillMaxSize(),
                    biasY = WallpaperBias,
                )
            }

            // 和玻璃内部同一层底色，保证虚化区域颜色对得上
            Box(Modifier.fillMaxSize().background(ScrimColor))
            AmbientGlow()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 44.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                RollerCard(
                    state = state,
                    controller = controller,
                    modifier = Modifier.widthIn(max = 920.dp).fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                HistoryRow(state = state, modifier = Modifier.widthIn(max = 920.dp).fillMaxWidth())
            }

            if (state.showHelp) {
                HelpOverlay { controller.toggleHelp() }
            }
        }
    }
}

@Composable
private fun AmbientGlow() {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AccentCyan.copy(alpha = 0.13f), Color.Transparent),
                center = Offset(size.width * 0.16f, size.height * 0.10f),
                radius = size.minDimension * 0.70f,
            ),
            radius = size.minDimension * 0.70f,
            center = Offset(size.width * 0.16f, size.height * 0.10f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AccentPink.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(size.width * 0.88f, size.height * 0.92f),
                radius = size.minDimension * 0.64f,
            ),
            radius = size.minDimension * 0.64f,
            center = Offset(size.width * 0.88f, size.height * 0.92f),
        )
    }
}

@Composable
private fun RollerCard(
    state: DiceUiState,
    controller: DiceController,
    modifier: Modifier = Modifier,
) {
    // 每次出新结果，玻璃上扫过一道高光
    val sheen = remember { Animatable(0f) }
    LaunchedEffect(state.results) {
        if (state.results.isNotEmpty() && !state.rolling) {
            sheen.snapTo(0f)
            sheen.animateTo(1f, tween(durationMillis = 1400, easing = LinearEasing))
        }
    }

    GlassSurface(
        modifier = modifier,
        cornerRadius = 34.dp,
        blurRadius = 22.dp,
        tint = Color(0xFF07060C).copy(alpha = 0.26f),
        darkness = 0.14f,
        rimAlpha = 0.72f,
        elevation = 34.dp,
        sheenProgress = sheen.value,
    ) {
        Column(Modifier.padding(horizontal = 30.dp, vertical = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Txt(text = "D I C E S", color = TextSecondary, size = 12.sp, weight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Txt(text = "壁纸 · ${state.wallpaperName}", color = TextTertiary, size = 11.sp, maxLines = 1)
            }

            Spacer(Modifier.height(18.dp))
            ResultZone(state)
            Spacer(Modifier.height(20.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.13f)))
            Spacer(Modifier.height(16.dp))
            InputBar(state, controller)
            Spacer(Modifier.height(10.dp))
            HintLine(state)
        }
    }
}

@Composable
private fun ResultZone(state: DiceUiState) {
    val results = state.results
    val single = results.singleOrNull()

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Txt(
                text = when {
                    results.isEmpty() -> "还没投过"
                    single == null -> "连投 ${results.size} 次 · 合计"
                    else -> "本次点数"
                },
                color = TextSecondary,
                size = 12.sp,
            )
            Spacer(Modifier.height(2.dp))
            if (results.isEmpty()) {
                EmptyDieGlyph()
            } else {
                Txt(
                    text = if (single != null) single.total.toString() else results.sumOf { it.total }.toString(),
                    color = TextPrimary,
                    size = 78.sp,
                    weight = FontWeight.Bold,
                    shadow = TitleShadow,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(4.dp))
            Txt(
                text = when {
                    results.isEmpty() -> "输入 2d6 试试，按按钮或回车"
                    single == null -> "${results.first().expression.trim()}   ·   每次独立计算"
                    else -> "${single.expression.trim()}   ·   共 ${single.diceCount} 个骰子"
                },
                color = TextSecondary,
                size = 12.sp,
                maxLines = 1,
            )
        }

        Spacer(Modifier.size(width = 20.dp, height = 1.dp))

        Box(Modifier.weight(1.25f), contentAlignment = Alignment.CenterStart) {
            when {
                results.isEmpty() -> EmptyHint()
                single == null -> RepeatTotals(results)
                else -> DiceChips(single)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DiceChips(result: RollResult) {
    val chips = result.groups.flatMap { group -> group.values.map { it to group.sides } }
    val shown = chips.take(60)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxLines = 3,
    ) {
        shown.forEach { (value, sides) ->
            DieChip(value = value, sides = sides)
        }
        if (chips.size > shown.size) {
            Box(
                Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Txt(text = "+${chips.size - shown.size}", color = TextSecondary, size = 12.sp)
            }
        }
        if (result.flat != 0) {
            Box(
                Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Gold.copy(alpha = 0.18f))
                    .border(1.dp, Gold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Txt(
                    text = if (result.flat > 0) "+${result.flat}" else "−${-result.flat}",
                    color = Gold,
                    size = 15.sp,
                    weight = FontWeight.Bold,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RepeatTotals(results: List<RollResult>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxLines = 4,
    ) {
        results.forEachIndexed { index, result ->
            Row(
                modifier = Modifier
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Txt(text = "${index + 1}", color = TextTertiary, size = 11.sp)
                Spacer(Modifier.size(width = 8.dp, height = 1.dp))
                Txt(text = result.total.toString(), color = TextPrimary, size = 18.sp, weight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmptyDieGlyph() {
    Canvas(Modifier.size(52.dp).padding(top = 6.dp)) {
        val stroke = size.width * 0.055f
        drawRoundRect(
            color = Color.White.copy(alpha = 0.38f),
            cornerRadius = CornerRadius(size.width * 0.24f),
            topLeft = Offset(stroke / 2f, stroke / 2f),
            size = Size(size.width - stroke, size.height - stroke),
            style = Stroke(width = stroke),
        )
        val radius = size.width * 0.05f
        listOf(
            Offset(0.30f, 0.30f),
            Offset(0.70f, 0.30f),
            Offset(0.50f, 0.50f),
            Offset(0.30f, 0.70f),
            Offset(0.70f, 0.70f),
        ).forEach { point ->
            drawCircle(
                color = Color.White.copy(alpha = 0.38f),
                radius = radius,
                center = Offset(size.width * point.x, size.height * point.y),
            )
        }
    }
}

@Composable
private fun EmptyHint() {
    Column {
        Txt(text = "2d6      2 个六面骰，取和", color = TextTertiary, size = 12.sp)
        Spacer(Modifier.height(6.dp))
        Txt(text = "d4 · d6 · d8 · d12 · d20 · 任意面数", color = TextTertiary, size = 12.sp)
        Spacer(Modifier.height(6.dp))
        Txt(text = "2d6+1d8+3   任意组合，全部相加", color = TextTertiary, size = 12.sp)
    }
}

@Composable
private fun DieChip(value: Int, sides: Int) {
    val accent = when {
        value == sides && sides > 1 -> Gold
        value == 1 -> ErrorRed
        else -> Color.White
    }
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.08f))
                )
            )
            .border(1.dp, accent.copy(alpha = if (accent == Color.White) 0.30f else 0.60f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (sides == 6) {
            DiePips(value = value, color = accent)
        } else {
            Txt(
                text = value.toString(),
                color = accent,
                size = if (value >= 100) 14.sp else 18.sp,
                weight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DiePips(value: Int, color: Color) {
    Canvas(Modifier.size(26.dp)) {
        val cell = size.width / 4f
        val radius = size.width * 0.085f
        fun pip(x: Int, y: Int) = drawCircle(
            color = color.copy(alpha = 0.95f),
            radius = radius,
            center = Offset(cell * (x + 1), cell * (y + 1)),
        )
        when (value) {
            1 -> pip(1, 1)
            2 -> {
                pip(0, 0)
                pip(2, 2)
            }

            3 -> {
                pip(0, 0)
                pip(1, 1)
                pip(2, 2)
            }

            4 -> {
                pip(0, 0)
                pip(2, 0)
                pip(0, 2)
                pip(2, 2)
            }

            5 -> {
                pip(0, 0)
                pip(2, 0)
                pip(1, 1)
                pip(0, 2)
                pip(2, 2)
            }

            6 -> {
                pip(0, 0)
                pip(2, 0)
                pip(0, 1)
                pip(2, 1)
                pip(0, 2)
                pip(2, 2)
            }

            else -> drawRoundRect(
                color = color.copy(alpha = 0.9f),
                cornerRadius = CornerRadius(size.width * 0.2f),
                style = Stroke(width = size.width * 0.09f),
            )
        }
    }
}

@Composable
private fun InputBar(state: DiceUiState, controller: DiceController) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        GlassSurface(
            modifier = Modifier.weight(1f).height(58.dp),
            cornerRadius = 22.dp,
            blurRadius = 20.dp,
            tint = Color(0xFF07060C).copy(alpha = 0.18f),
            darkness = 0.10f,
            rimAlpha = 0.55f,
            elevation = 8.dp,
            backdrop = false,
        ) {
            Box(
                Modifier.fillMaxSize().padding(horizontal = 22.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                BasicTextField(
                    value = state.input,
                    onValueChange = { text ->
                        state.input = text
                        state.error = null
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(AccentCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent { event ->
                            when {
                                event.type != KeyEventType.KeyDown -> false
                                event.key == Key.Enter || event.key == Key.NumPadEnter -> {
                                    controller.submit()
                                    true
                                }

                                event.key == Key.Escape -> {
                                    state.showHelp = false
                                    true
                                }

                                else -> false
                            }
                        },
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (state.input.isEmpty()) {
                                Txt(
                                    text = "2d6   ·   1d20   ·   3d8+2d6   ·   /bg 换背景   ·   ? 看说明",
                                    color = TextTertiary,
                                    size = 15.sp,
                                    maxLines = 1,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }

        Spacer(Modifier.size(width = 12.dp, height = 1.dp))
        RollButton(rolling = state.rolling) {
            controller.submit()
            runCatching { focusRequester.requestFocus() }
        }
    }
}

@Composable
private fun RollButton(rolling: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.90f else 1f, label = "press")
    val tilt by animateFloatAsState(if (rolling) 14f else 0f, label = "tilt")

    Box(Modifier.size(58.dp).graphicsLayer { scaleX = scale; scaleY = scale }) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 29.dp,
            blurRadius = 18.dp,
            tint = Color.White.copy(alpha = 0.26f),
            darkness = 0.06f,
            rimAlpha = 0.90f,
            elevation = 16.dp,
            backdrop = false,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable(interactionSource = interaction, indication = null, onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.size(28.dp).rotate(tilt)) {
                    val stroke = size.width * 0.085f
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.95f),
                        cornerRadius = CornerRadius(size.width * 0.24f),
                        topLeft = Offset(stroke / 2f, stroke / 2f),
                        size = Size(size.width - stroke, size.height - stroke),
                        style = Stroke(width = stroke),
                    )
                    val r = size.width * 0.075f
                    listOf(
                        Offset(size.width * 0.30f, size.height * 0.30f),
                        Offset(size.width * 0.50f, size.height * 0.50f),
                        Offset(size.width * 0.70f, size.height * 0.70f),
                    ).forEach { drawCircle(color = Color.White.copy(alpha = 0.95f), radius = r, center = it) }
                }
            }
        }
    }
}

@Composable
private fun HintLine(state: DiceUiState) {
    val error = state.error
    if (error != null) {
        Txt(text = error, color = ErrorRed, size = 12.sp, maxLines = 2)
        return
    }

    val parsed = remember(state.input) {
        state.input.takeIf { it.isNotBlank() && !it.trim().startsWith("/") }
            ?.let { runCatching { RollParser.parse(it) }.getOrNull() }
    }

    val text = when {
        state.rolling -> "投掷中……"
        parsed != null -> buildString {
            append("${parsed.diceCount} 个骰子")
            append("   ·   范围 ${parsed.minValue} ~ ${parsed.maxValue}")
            if (parsed.repeat > 1) append("   ·   连投 ${parsed.repeat} 次")
            append("   ·   回车或点按钮")
        }

        else -> "支持 d4/d6/d8/d12/d20 任意面数 · /bg 换背景图 · ? 看说明"
    }

    Txt(text = text, color = TextTertiary, size = 12.sp, maxLines = 1)
}

@Composable
private fun HistoryRow(state: DiceUiState, modifier: Modifier = Modifier) {
    if (state.history.isEmpty()) return
    GlassSurface(
        modifier = modifier.height(44.dp),
        cornerRadius = 22.dp,
        blurRadius = 16.dp,
        tint = Color(0xFF07060C).copy(alpha = 0.20f),
        darkness = 0.10f,
        rimAlpha = 0.46f,
        elevation = 6.dp,
        backdrop = false,
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Txt(text = "最近", color = TextTertiary, size = 11.sp)
            state.history.take(6).forEach { entry ->
                Txt(text = entry, color = TextSecondary, size = 12.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun HelpOverlay(onDismiss: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        GlassSurface(
            modifier = Modifier.widthIn(max = 660.dp).fillMaxWidth(0.72f),
            cornerRadius = 28.dp,
            blurRadius = 34.dp,
            tint = Color(0xFF07060C).copy(alpha = 0.42f),
            darkness = 0.16f,
            rimAlpha = 0.78f,
            elevation = 40.dp,
        ) {
            Column(Modifier.padding(28.dp)) {
                Txt(text = "怎么用", color = TextPrimary, size = 20.sp, weight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                HelpLine("d6 / D20", "投一个骰子，面数随便写")
                HelpLine("3d6", "同时投 3 个六面骰，点数相加")
                HelpLine("2d6 + 1d8 + 3", "任意组合，最后全部相加")
                HelpLine("d6 x5", "连投 5 次，每次单独出结果")
                HelpLine("/bg", "挑一张自己的图片当背景")
                HelpLine("/bg 路径", "也可以直接指定图片文件")
                HelpLine("/bg reset", "恢复默认背景")
                HelpLine("/clear", "清空记录")
                HelpLine("?", "打开 / 收起这份说明")
                Spacer(Modifier.height(16.dp))
                Txt(text = "点任意位置关闭", color = TextTertiary, size = 11.sp)
            }
        }
    }
}

@Composable
private fun HelpLine(keys: String, description: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Txt(
            text = keys,
            color = AccentCyan,
            size = 13.sp,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            align = TextAlign.Start,
            modifier = Modifier.widthIn(min = 150.dp),
        )
        Txt(text = description, color = TextSecondary, size = 13.sp, maxLines = 2)
    }
}
