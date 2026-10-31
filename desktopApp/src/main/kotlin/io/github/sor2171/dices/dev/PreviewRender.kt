package io.github.sor2171.dices.dev

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import io.github.sor2171.dices.core.RollParser
import io.github.sor2171.dices.core.Roller
import io.github.sor2171.dices.ui.DiceAppContent
import io.github.sor2171.dices.ui.DiceUiState
import io.github.sor2171.dices.ui.WallpaperStore
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/**
 * 开发用：把界面离屏渲染成 PNG，不用开窗口就能检查玻璃效果。
 * 用法：gradlew :desktopApp:renderPreview -Pout=<输出目录>
 */@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) {
    checkParser()
    val outDir = File(args.getOrElse(0) { "." })
    val width = args.getOrElse(1) { "1120" }.toIntOrNull() ?: 1120
    val height = args.getOrElse(2) { "820" }.toIntOrNull() ?: 820
    outDir.mkdirs()

    val wallpaper = WallpaperStore.loadBuiltIn()
    println("preview: wallpaper=${wallpaper?.width}x${wallpaper?.height} out=${outDir.absolutePath}")

    fun baseState(): DiceUiState =
        DiceUiState(initialWallpaper = wallpaper, initialName = "默认壁纸")

    val scenes = linkedMapOf<String, DiceUiState>(
        "default" to baseState().apply {
            results = Roller.roll(RollParser.parse("2d6 + 1d8 + 3"))
            expression = "2d6 + 1d8 + 3"
            input = "2d6 + 1d8 + 3"
            history = listOf("2d6 → 9", "d20 → 17", "1d8+2 → 7")
        },
        "multi" to baseState().apply {
            results = Roller.roll(RollParser.parse("d6 x5"))
            expression = "d6 x5"
            input = "d6 x5"
            history = listOf("2d6 → 9", "d20 → 17", "d6 x5 → 4/2/6/1/5")
        },
        "empty" to baseState().apply {
            history = listOf("2d6 → 9", "d20 → 17")
        },
        "error" to baseState().apply {
            input = "3x6"
            error = "看不懂 “6”，可以写 2d6、d20+3、1d8 x4"
        },
        "help" to baseState().apply {
            results = Roller.roll(RollParser.parse("2d6 + 1d8 + 3"))
            expression = "2d6 + 1d8 + 3"
            input = "?"
            showHelp = true
        },
    )

    scenes.forEach { (name, state) ->
        val scene = ImageComposeScene(width, height, Density(1f)) { DiceAppContent(state) }
        var nanos = 0L
        var image = scene.render(nanos)
        repeat(40) {
            nanos += 16_000_000L
            image = scene.render(nanos)
        }
        val start = System.nanoTime()
        repeat(60) {
            nanos += 16_000_000L
            image = scene.render(nanos)
        }
        val perFrame = (System.nanoTime() - start) / 60.0 / 1_000_000.0
        println("frame cost[$name]: %.2f ms".format(perFrame))
        val file = File(outDir, "preview_$name.png")
        image.encodeToData(EncodedImageFormat.PNG)?.bytes?.let { file.writeBytes(it) }
        scene.close()
        println("preview written: ${file.absolutePath}")
    }
}

private fun checkParser() {
    data class Case(val input: String, val expected: String)

    val cases = listOf(
        Case("2d6", "2d6 flat=0 x1"),
        Case("d20", "1d20 flat=0 x1"),
        Case("3d8+2", "3d8 flat=2 x1"),
        Case("2d6 + 1d8 + 3", "2d6|1d8 flat=3 x1"),
        Case("d6 x5", "1d6 flat=0 x5"),
        Case("2D6", "2d6 flat=0 x1"),
        Case("1d4，2d6", "1d4|2d6 flat=0 x1"),
        Case("2d6-3", "2d6 flat=-3 x1"),
        Case("3x6", "ERROR"),
        Case("", "ERROR"),
        Case("0d6", "ERROR"),
        Case("1000d6", "ERROR"),
        Case("d", "ERROR"),
    )

    var passed = 0
    for (case in cases) {
        val actual = try {
            val parsed = RollParser.parse(case.input)
            parsed.groups.joinToString("|") { "${it.count}d${it.sides}" } +
                " flat=${parsed.flat} x${parsed.repeat}"
        } catch (exception: Exception) {
            "ERROR"
        }
        val ok = actual == case.expected
        if (ok) passed++
        println("parser ${if (ok) "OK  " else "FAIL"} [${case.input}] -> $actual (期望 ${case.expected})")
    }
    println("parser: $passed/${cases.size} 通过")
}
