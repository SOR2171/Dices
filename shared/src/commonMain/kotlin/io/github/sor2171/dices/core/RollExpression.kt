package io.github.sor2171.dices.core

import kotlin.random.Random

/** 一组骰子，例如 3d6 */
data class DiceGroup(val count: Int, val sides: Int)

/** 解析后的表达式：若干组骰子 + 一个平值修正 + 连投次数 */
data class ParsedExpression(
    val raw: String,
    val groups: List<DiceGroup>,
    val flat: Int,
    val repeat: Int,
) {
    val diceCount: Int get() = groups.sumOf { it.count }

    /** 理论最小 / 最大值（含平值修正） */
    val minValue: Int get() = groups.sumOf { it.count } + flat
    val maxValue: Int get() = groups.sumOf { it.count * it.sides } + flat
}

class ExpressionException(message: String) : Exception(message)

data class GroupResult(val count: Int, val sides: Int, val values: List<Int>) {
    val subtotal: Int get() = values.sum()
}

data class RollResult(
    val expression: String,
    val groups: List<GroupResult>,
    val flat: Int,
) {
    val total: Int get() = groups.sumOf { it.subtotal } + flat
    val diceCount: Int get() = groups.sumOf { it.count }
}

/**
 * 把用户写的一句话拆成骰子组。
 *
 * 支持：
 *   d6 / D6 / 20d6      -> 若干个 m 面骰
 *   2d6+1d8+3           -> 任意组合，最后取和
 *   2d6, 1d8, -2        -> 逗号、加号、减号、空格都能当分隔
 *   d6 x5 / 3d8 *2      -> 连投 n 次，每次单独出一个结果
 */
object RollParser {
    private const val MAX_DICE_PER_GROUP = 200
    private const val MAX_SIDES = 100000
    private const val MAX_TOTAL_DICE = 500
    private const val MAX_REPEAT = 100

    private val diceTerm = Regex("^([0-9]*)[dD]([0-9]+)$")
    private val flatTerm = Regex("^[0-9]+$")
    private val repeatTail = Regex("[xX×*]\\s*([0-9]+)$")

    fun parse(rawInput: String): ParsedExpression {
        val raw = rawInput.trim()
        if (raw.isEmpty()) throw ExpressionException("先写点东西吧，比如 2d6")

        var body = raw
            .replace('，', ',')
            .replace('＋', '+')
            .replace('－', '-')
            .replace('　', ' ')
            .replace('ｄ', 'd')
            .replace('Ｄ', 'D')

        var repeat = 1
        repeatTail.find(body)?.let { match ->
            val times = match.groupValues[1].toIntOrNull()
                ?: throw ExpressionException("重复次数看不懂：${match.value}")
            if (times !in 1..MAX_REPEAT) throw ExpressionException("重复次数要在 1 到 $MAX_REPEAT 之间")
            repeat = times
            body = body.substring(0, match.range.first)
        }

        val tokens = tokenize(body)
        if (tokens.isEmpty()) throw ExpressionException("没找到骰子，试试 d6 或者 3d6")

        val groups = mutableListOf<DiceGroup>()
        var flat = 0
        var totalDice = 0

        for ((sign, text) in tokens) {
            val diceMatch = diceTerm.matchEntire(text)
            val flatMatch = flatTerm.matchEntire(text)
            when {
                diceMatch != null -> {
                    if (sign < 0) throw ExpressionException("骰子前面不能带减号")
                    val countText = diceMatch.groupValues[1]
                    val count = if (countText.isEmpty()) 1 else countText.toIntOrNull()
                        ?: throw ExpressionException("骰子个数太大：$countText")
                    val sides = diceMatch.groupValues[2].toIntOrNull()
                        ?: throw ExpressionException("骰子面数太大：${diceMatch.groupValues[2]}")
                    if (count !in 1..MAX_DICE_PER_GROUP) throw ExpressionException("一组最多 $MAX_DICE_PER_GROUP 个骰子")
                    if (sides !in 1..MAX_SIDES) throw ExpressionException("面数要在 1 到 $MAX_SIDES 之间")
                    totalDice += count
                    if (totalDice > MAX_TOTAL_DICE) throw ExpressionException("一次最多 $MAX_TOTAL_DICE 个骰子，别太贪心")
                    groups += DiceGroup(count, sides)
                }

                flatMatch != null -> {
                    val value = text.toIntOrNull() ?: throw ExpressionException("数值太大：$text")
                    flat += sign * value
                }

                else -> throw ExpressionException("看不懂 “$text”，可以写 2d6、d20+3、1d8 x4")
            }
        }

        if (groups.isEmpty()) throw ExpressionException("至少要有一个骰子，比如 d6")
        return ParsedExpression(raw = raw, groups = groups, flat = flat, repeat = repeat)
    }

    /** 拆出 "符号 + 词" 的序列 */
    private fun tokenize(body: String): List<Pair<Int, String>> {
        val tokens = mutableListOf<Pair<Int, String>>()
        val buffer = StringBuilder()
        var sign = 1

        fun flush() {
            val text = buffer.toString().trim()
            if (text.isNotEmpty()) tokens += sign to text
            buffer.setLength(0)
            sign = 1
        }

        var index = 0
        while (index < body.length) {
            when (val char = body[index]) {
                ',', '+' -> {
                    flush()
                    index++
                }

                '-' -> {
                    flush()
                    sign = -1
                    index++
                }

                else -> {
                    if (char.isWhitespace()) flush() else buffer.append(char)
                    index++
                }
            }
        }
        flush()
        return tokens
    }
}

object Roller {
    fun roll(parsed: ParsedExpression, random: Random = Random.Default): List<RollResult> =
        List(parsed.repeat) {
            RollResult(
                expression = parsed.raw,
                groups = parsed.groups.map { group ->
                    GroupResult(
                        count = group.count,
                        sides = group.sides,
                        values = List(group.count) { random.nextInt(1, group.sides + 1) },
                    )
                },
                flat = parsed.flat,
            )
        }
}
