package io.github.sor2171.dices.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

/** 当前壁纸，玻璃面板要从它身上「取景」来做毛玻璃 */
val LocalBackdropImage = compositionLocalOf<ImageBitmap?> { null }

/** 壁纸铺满的整个窗口尺寸（px） */
val LocalBackdropSize = compositionLocalOf { Size.Zero }

/** 盖在壁纸上的一层底色，玻璃里也要画同一层，颜色才对得上 */
val LocalBackdropScrim = compositionLocalOf { Color(0x5E0B0B14) }

/** 竖图裁切时偏上还是偏下：0 = 顶部，1 = 底部 */
val LocalBackdropBias = compositionLocalOf { 0.5f }

/**
 * 按「和窗口壁纸完全一致」的裁切方式画一遍壁纸。
 * 玻璃面板靠它取景，才能做出真正的背景虚化。
 */
internal fun DrawScope.drawBackdropCopy(
    image: ImageBitmap,
    rootSize: Size,
    localOrigin: Offset,
    scrim: Color,
    colorFilter: ColorFilter? = null,
    biasY: Float = 0.5f,
) {
    if (rootSize.width <= 0f || rootSize.height <= 0f) return
    val imageWidth = image.width.toFloat()
    val imageHeight = image.height.toFloat()
    val scale = max(rootSize.width / imageWidth, rootSize.height / imageHeight)
    val targetWidth = imageWidth * scale
    val targetHeight = imageHeight * scale
    val offsetX = (rootSize.width - targetWidth) / 2f - localOrigin.x
    val offsetY = (rootSize.height - targetHeight) * biasY - localOrigin.y

    drawImage(
        image = image,
        srcOffset = IntOffset.Zero,
        srcSize = IntSize(image.width, image.height),
        dstOffset = IntOffset(offsetX.roundToInt(), offsetY.roundToInt()),
        dstSize = IntSize(
            targetWidth.roundToInt().coerceAtLeast(1),
            targetHeight.roundToInt().coerceAtLeast(1),
        ),
        colorFilter = colorFilter,
        filterQuality = FilterQuality.Low,
    )
    if (scrim.alpha > 0f) {
        drawRect(
            color = scrim,
            topLeft = Offset(-localOrigin.x, -localOrigin.y),
            size = rootSize,
        )
    }
}

/** 铺满整窗的壁纸层 */
@Composable
fun WallpaperLayer(image: ImageBitmap, modifier: Modifier = Modifier, biasY: Float = 0.5f) {
    Canvas(modifier) {
        drawBackdropCopy(
            image = image,
            rootSize = size,
            localOrigin = Offset.Zero,
            scrim = Color.Transparent,
            biasY = biasY,
        )
    }
}

/** 没有壁纸时的兜底底色 */
val FallbackGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF141A2C), Color(0xFF24122B), Color(0xFF0B0E17)),
)

/**
 * 液态玻璃面板：
 * 1. 把身后的壁纸按同样位置画一遍并模糊，真正的背景虚化
 * 2. 叠半透明白 + 上下渐变，做出厚度
 * 3. 边角加一点虹彩，像是光在玻璃里折射
 * 4. 内外描边模拟玻璃边缘的亮线
 * 5. 可选的高光缓慢游走，看起来像液体
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    blurRadius: Dp = 26.dp,
    tint: Color = Color.White.copy(alpha = 0.08f),
    darkness: Float = 0.10f,
    rimAlpha: Float = 0.62f,
    elevation: Dp = 26.dp,
    saturation: Float = 1.12f,
    backdrop: Boolean = true,
    sheenProgress: Float? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val image = LocalBackdropImage.current
    val rootSize = LocalBackdropSize.current
    val scrim = LocalBackdropScrim.current
    val biasY = LocalBackdropBias.current
    var bounds by remember { mutableStateOf(Rect.Zero) }
    val shape = RoundedCornerShape(cornerRadius)
    val glassFilter = remember(saturation) {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(saturation) })
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.32f),
                spotColor = Color.Black.copy(alpha = 0.46f),
            )
            .onGloballyPositioned { coordinates -> bounds = coordinates.boundsInRoot() }
            .clip(shape)
    ) {
        // 1. 背景虚化
        if (backdrop && image != null) {
            Canvas(
                Modifier
                    .matchParentSize()
                    .blur(blurRadius, BlurredEdgeTreatment.Unbounded)
            ) {
                drawBackdropCopy(
                    image = image,
                    rootSize = rootSize,
                    localOrigin = bounds.topLeft,
                    scrim = scrim,
                    colorFilter = glassFilter,
                    biasY = biasY,
                )
            }
        } else if (!backdrop) {
            Box(Modifier.matchParentSize().background(Color(0xFF0B0A12).copy(alpha = 0.24f)))
        } else {
            Box(Modifier.matchParentSize().background(FallbackGradient))
        }

        // 2. 厚度
        Box(Modifier.matchParentSize().background(tint))
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.01f),
                            Color.Black.copy(alpha = darkness),
                        )
                    )
                )
        )

        // 3. 边角的虹彩
        Canvas(Modifier.matchParentSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6FE3FF).copy(alpha = 0.09f), Color.Transparent),
                    center = Offset(0f, size.height),
                    radius = size.width * 0.55f,
                )
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF8AD0).copy(alpha = 0.07f), Color.Transparent),
                    center = Offset(size.width, size.height * 0.05f),
                    radius = size.width * 0.5f,
                )
            )
        }

        // 4. 液体的高光：平时是静的高光，投掷时扫过一道
        LiquidSheen(sheenProgress, rimAlpha)

        // 5. 边缘亮线
        Canvas(Modifier.matchParentSize()) {
            val stroke = 1.4.dp.toPx()
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = rimAlpha),
                        Color.White.copy(alpha = rimAlpha * 0.16f),
                        Color(0xFFA9F2FF).copy(alpha = rimAlpha * 0.30f),
                        Color.White.copy(alpha = rimAlpha * 0.62f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                ),
                topLeft = Offset(stroke / 2f, stroke / 2f),
                size = Size(
                    (size.width - stroke).coerceAtLeast(0f),
                    (size.height - stroke).coerceAtLeast(0f),
                ),
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                style = Stroke(width = stroke),
            )
            // 顶边被光打亮的一小道
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.50f),
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent,
                    ),
                    startY = 0f,
                    endY = size.height * 0.55f,
                ),
                topLeft = Offset(stroke * 1.2f, stroke * 1.2f),
                size = Size(
                    (size.width - stroke * 2.4f).coerceAtLeast(0f),
                    (size.height - stroke * 2.4f).coerceAtLeast(0f),
                ),
                cornerRadius = CornerRadius(cornerRadius.toPx() - stroke, cornerRadius.toPx() - stroke),
                style = Stroke(width = stroke * 0.8f),
            )
            // 底部内侧压暗一点，做出厚度
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.22f),
                    ),
                    startY = 0f,
                    endY = size.height,
                ),
                topLeft = Offset(stroke * 4f, stroke * 4f),
                size = Size(
                    (size.width - stroke * 8f).coerceAtLeast(0f),
                    (size.height - stroke * 8f).coerceAtLeast(0f),
                ),
                cornerRadius = CornerRadius(cornerRadius.toPx() - stroke * 3f, cornerRadius.toPx() - stroke * 3f),
                style = Stroke(width = stroke * 5f),
            )
        }

        content()
    }
}

@Composable
private fun BoxScope.LiquidSheen(progress: Float?, rimAlpha: Float) {
    Canvas(Modifier.matchParentSize()) {
        if (progress != null) {
            val bandWidth = size.width * 0.34f
            val centerX = (-0.35f + progress * 1.7f) * size.width
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.09f),
                        Color.Transparent,
                    ),
                    start = Offset(centerX - bandWidth / 2f, 0f),
                    end = Offset(centerX + bandWidth / 2f, size.height * 0.9f),
                )
            )
        }
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.10f * rimAlpha + 0.02f),
                    Color.Transparent,
                ),
                center = Offset(size.width * 0.20f, -size.height * 0.12f),
                radius = size.width * 0.5f,
            ),
            topLeft = Offset(-size.width * 0.22f, -size.height * 0.58f),
            size = Size(size.width * 0.8f, size.height * 0.85f),
        )
    }
}
