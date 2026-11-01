@Composable
fun DiceValueCard(
    data: DiceData,
    rollToken: Boolean,
) {
    // data.diceType 是 "D6" 这种字符串，这里取出面数
    val sides = data.diceType.removePrefix("D").toIntOrNull()?.coerceAtLeast(2) ?: 6

    // 卡片当前显示的点数。
    // 投掷时先随机变化，0.5 秒后才变成 data.value
    var shownValue by remember { mutableStateOf(data.value) }

    // 第一次进入页面不播放动画，避免一打开所有骰子一起转
    var firstComposition by remember { mutableStateOf(true) }

    // 控制骰子旋转，0 -> 360 度
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(rollToken) {
        // rollToken 变化 = 用户点了一次 ROLL
        if (firstComposition) {
            firstComposition = false
            shownValue = data.value
            return@LaunchedEffect
        }

        // 每次投掷都从 0 度重新开始
        rotation.snapTo(0f)

        // 和下面的随机点数同时执行：旋转 0.5 秒
        launch {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearEasing,
                ),
            )
        }

        // 每 100ms 随机换一个面，5 次刚好 0.5 秒
        repeat(5) {
            delay(100)
            shownValue = Random.nextInt(1, sides + 1)
        }

        // 动画结束，显示真正的投掷结果
        shownValue = data.value
    }

    // Surface 就是骰子本体：
    // 正方形、圆角、米白底、边框和阴影
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFFFBF0),
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0x33000000)),
        modifier = Modifier
            .padding(6.dp)
            .aspectRatio(1f)
            .graphicsLayer {
                // 只旋转绘制层，不影响网格布局
                rotationZ = rotation.value
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val side = min(size.width, size.height)

                if (sides == 6) {
                    // D6 画圆点
                    drawPips(shownValue, side)
                } else {
                    // D4/D8/D20 等画一个多边形轮廓
                    drawPath(
                        path = polygonPath(sides, side),
                        color = Color(0x22000000),
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            }

            // 非 D6 显示数字；D6 还没投掷时显示 "—"
            if (sides != 6 || shownValue !in 1..6) {
                Text(
                    text = if (shownValue == 0) "—" else shownValue.toString(),
                    color = Color(0xFF242424),
                    fontSize = if (shownValue.toString().length > 2) 26.sp else 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            // 左上角基本信息
            Text(
                text = data.diceType,
                color = Color(0xFF7A6E5F),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.TopStart),
            )

            // 右上角编号
            Text(
                text = "No.${data.id}",
                color = Color(0xFF9A8E7D),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

// 画 D6 的圆点。坐标都是相对骰面尺寸的比例，所以缩放不会错位
private fun DrawScope.drawPips(value: Int, side: Float) {
    val points = when (value) {
        1 -> listOf(0.5f to 0.5f)
        2 -> listOf(0.28f to 0.28f, 0.72f to 0.72f)
        3 -> listOf(0.26f to 0.26f, 0.5f to 0.5f, 0.74f to 0.74f)
        4 -> listOf(
            0.28f to 0.28f, 0.72f to 0.28f,
            0.28f to 0.72f, 0.72f to 0.72f,
        )
        5 -> listOf(
            0.28f to 0.28f, 0.72f to 0.28f,
            0.5f to 0.5f,
            0.28f to 0.72f, 0.72f to 0.72f,
        )
        6 -> listOf(
            0.3f to 0.23f, 0.7f to 0.23f,
            0.3f to 0.5f, 0.7f to 0.5f,
            0.3f to 0.77f, 0.7f to 0.77f,
        )
        else -> emptyList()
    }

    val radius = side * 0.075f
    points.forEach { (x, y) ->
        drawCircle(
            color = Color(0xFF242424),
            radius = radius,
            center = Offset(side * x, side * y),
        )
    }
}

// 不同面数用不同边数的多边形近似：
// D4 三角形、D8 菱形、D12 五边形、D20 六边形
private fun polygonPath(sides: Int, side: Float): Path {
    val vertices = when (sides) {
        4 -> 3
        8 -> 4
        10, 12 -> 5
        else -> 6
    }
    val center = side / 2f
    val radius = side * 0.42f

    return Path().apply {
        repeat(vertices) { index ->
            val angle = -PI / 2.0 + 2.0 * PI * index / vertices
            val x = center + radius * cos(angle).toFloat()
            val y = center + radius * sin(angle).toFloat()
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
}

@Preview
@Composable
fun DiceValueCardPreview() {
    DiceValueCard(
        data = DiceData(1, "D6", 5),
        rollToken = false,
    )
}