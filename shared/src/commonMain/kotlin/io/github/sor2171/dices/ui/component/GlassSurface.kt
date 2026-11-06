package io.github.sor2171.dices.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dices.shared.generated.resources.Res
import dices.shared.generated.resources.table_background
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

// 每个面板按窗口坐标取同一张背景，滚动时保持图像对齐。
@Composable
fun GlassSurface(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    var position by remember { mutableStateOf(Offset.Zero) }
    val viewport = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier.onGloballyPositioned { position = it.positionInRoot() }
            .clip(shape)
            .border(
                BorderStroke(1.dp, Brush.linearGradient(listOf(
                    Color.White.copy(alpha = .44f),
                    Color.White.copy(alpha = .10f),
                    Color(0xFFD6BF92).copy(alpha = .35f),
                ))),
                shape,
            ),
    ) {
        Box(Modifier.matchParentSize().clip(shape)) {
            if (viewport.width > 0 && viewport.height > 0) {
                Image(
                    painterResource(Res.drawable.table_background),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset { IntOffset(-position.x.roundToInt(), -position.y.roundToInt()) }
                        .wrapContentSize(Alignment.TopStart, unbounded = true)
                        .requiredSize(
                            with(density) { viewport.width.toDp() },
                            with(density) { viewport.height.toDp() },
                        )
                        .blur(3.dp),
                )
            }
        }
        Box(Modifier.matchParentSize().background(
            Brush.verticalGradient(listOf(
                Color(0xFF10221C).copy(alpha = .30f),
                Color(0xFF0D1D17).copy(alpha = .34f),
                Color(0xFF0A1813).copy(alpha = .46f),
            ))
        ))
        Box(Modifier.matchParentSize().background(
            Brush.linearGradient(
                0f to Color.White.copy(alpha = .11f),
                .30f to Color.White.copy(alpha = .025f),
                .55f to Color.Transparent,
                1f to Color.White.copy(alpha = .045f),
                start = Offset.Zero,
                end = Offset(1000f, 700f),
            )
        ))
        content()
    }
}
