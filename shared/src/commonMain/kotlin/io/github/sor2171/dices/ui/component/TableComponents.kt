package io.github.sor2171.dices.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.github.sor2171.dices.ui.theme.Palette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    GlassSurface(modifier) {
        Column(Modifier.padding(24.dp), content = content)
    }
}

// 玻璃桌面的细线装饰，不覆盖背后的照片。
@Composable
fun TableFelt(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val inset = 15.dp.toPx()
        drawRect(
            Palette.Gold.copy(alpha = 0.16f),
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2, size.height - inset * 2),
            style = Stroke(0.7.dp.toPx()),
        )
        val lineLength = 18.dp.toPx()
        val corners = listOf(
            Offset(inset, inset), Offset(size.width - inset, inset),
            Offset(inset, size.height - inset), Offset(size.width - inset, size.height - inset),
        )
        corners.forEachIndexed { index, corner ->
            val dx = if (index % 2 == 0) 1 else -1
            val dy = if (index < 2) 1 else -1
            drawLine(Palette.Gold, corner, corner + Offset(lineLength * dx, 0f), 1.5.dp.toPx())
            drawLine(Palette.Gold, corner, corner + Offset(0f, lineLength * dy), 1.5.dp.toPx())
        }
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(Palette.Gold.copy(alpha = 0.045f), size.minDimension * 0.34f, center, style = Stroke(1.dp.toPx()))
        drawCircle(Palette.Gold.copy(alpha = 0.025f), size.minDimension * 0.36f, center, style = Stroke(0.7.dp.toPx()))

    }
}

@Composable
fun IvoryDie(sides: Int, value: Int, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val side = size.minDimension
        drawOval(
            Brush.radialGradient(listOf(Color.Black.copy(alpha = 0.60f), Color.Transparent), center = Offset(side * .5f, side * .86f), radius = side * .44f),
            topLeft = Offset(side * 0.1f, side * 0.76f),
            size = Size(side * 0.85f, side * 0.23f),
        )
        if (sides == 6) drawCube(side, value) else drawPolyhedron(side, sides)
    }
}

private fun DrawScope.drawCube(side: Float, value: Int) {
    val front = listOf(Offset(.16f, .30f), Offset(.68f, .24f), Offset(.69f, .80f), Offset(.18f, .88f))
    val top = listOf(Offset(.16f, .30f), Offset(.38f, .14f), Offset(.88f, .12f), Offset(.68f, .24f))
    val right = listOf(Offset(.68f, .24f), Offset(.88f, .12f), Offset(.90f, .67f), Offset(.69f, .80f))
    val silhouette = listOf(front[0], top[1], top[2], right[2], front[2], front[3])
    drawPath(roundedPolygon(silhouette, side), Color(0xFFD7C6A3))
    drawPath(roundedPolygon(top, side), Brush.linearGradient(listOf(Color(0xFFFFF6E1), Color(0xFFDACAA6))))
    drawPath(roundedPolygon(right, side), Brush.linearGradient(listOf(Color(0xFFC5B18B), Color(0xFF8B7956))))
    drawPath(roundedPolygon(front, side), Brush.linearGradient(listOf(Color(0xFFFCF2DB), Palette.Paper, Color(0xFFC4B18C))))
    drawPath(roundedPolygon(front, side), Color(0xFFFBEDC9).copy(alpha = 0.60f), style = Stroke(1.dp.toPx()))
    drawPath(roundedPolygon(top, side), Color(0xFFFFF3D8).copy(alpha = 0.55f), style = Stroke(0.8.dp.toPx()))
    val positions = when (value) {
        1 -> listOf(1 to 1)
        2 -> listOf(0 to 0, 2 to 2)
        3 -> listOf(0 to 0, 1 to 1, 2 to 2)
        4 -> listOf(0 to 0, 2 to 0, 0 to 2, 2 to 2)
        5 -> listOf(0 to 0, 2 to 0, 1 to 1, 0 to 2, 2 to 2)
        6 -> listOf(0 to 0, 2 to 0, 0 to 1, 2 to 1, 0 to 2, 2 to 2)
        else -> emptyList()
    }
    positions.forEach { (x, y) ->
        val u = .23f + x * .27f
        val v = .23f + y * .27f
        val left = front[0] * (1 - v) + front[3] * v
        val rightPoint = front[1] * (1 - v) + front[2] * v
        val point = (left * (1 - u) + rightPoint * u) * side
        drawCircle(Color(0xFFFFF3D0).copy(alpha = .55f), side * .039f, point + Offset(0f, side * .008f))
        drawCircle(
            Brush.radialGradient(listOf(Color(0xFF0D1511), Color(0xFF534936)), center = point - Offset(side * .01f, side * .01f), radius = side * .05f),
            side * .034f, point,
        )
    }
}

private fun DrawScope.drawPolyhedron(side: Float, sides: Int) {
    val vertices = when (sides) {
        4 -> 3
        8 -> 4
        10, 12 -> 5
        else -> 6
    }
    val center = Offset(.50f, .48f)
    val points = List(vertices) { index ->
        val angle = -PI / 2 + 2 * PI * index / vertices
        center + Offset((cos(angle) * .36).toFloat(), (sin(angle) * .36).toFloat())
    }
    drawPath(polygon(points, side), Brush.linearGradient(listOf(Color(0xFFF5E7C7), Color(0xFFB49A67))))
    points.indices.forEach { index ->
        val next = points[(index + 1) % points.size]
        drawPath(
            polygon(listOf(points[index], next, center), side),
            if (index % 2 == 0) Color.White.copy(alpha = .16f) else Color(0xFF755C32).copy(alpha = .10f),
        )
        drawLine(Color(0xFF8E7548).copy(alpha = .4f), points[index] * side, center * side, .7.dp.toPx())
    }
    drawPath(polygon(points, side), Color(0xFFFFF0CC), style = Stroke(1.2.dp.toPx()))
    drawCircle(Brush.radialGradient(listOf(Color(0xFFF0DFB8), Color(0xFFE1CCA0))), side * .18f, center * side)
}

private fun polygon(points: List<Offset>, side: Float): Path = Path().apply {
    moveTo(points.first().x * side, points.first().y * side)
    points.drop(1).forEach { lineTo(it.x * side, it.y * side) }
    close()
}

private fun roundedPolygon(points: List<Offset>, side: Float): Path {
    val path = Path()
    val rounding = .09f
    points.forEachIndexed { index, vertex ->
        val previous = points[(index + points.size - 1) % points.size]
        val next = points[(index + 1) % points.size]
        val entry = (vertex * (1 - rounding) + previous * rounding) * side
        val exit = (vertex * (1 - rounding) + next * rounding) * side
        if (index == 0) path.moveTo(entry.x, entry.y) else path.lineTo(entry.x, entry.y)
        path.quadraticTo(vertex.x * side, vertex.y * side, exit.x, exit.y)
    }
    path.close()
    return path
}
