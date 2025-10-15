package com.github.sor2171.dices.data

import kotlin.random.Random
import kotlin.random.nextInt

class DiceType(
    val max: Int
) {
    val name: String = "D$max"
    val diceValue = mutableListOf(0)

    fun roll() {
        for (i in diceValue.indices) {
            diceValue[i] = Random.nextInt(1..max)
        }
    }

    fun mountTo(mount: Int) {
        when {
            mount < diceValue.size -> mountMinusTo(mount)
            mount > diceValue.size -> mountAddTo(mount)
        }
    }

    private fun mountAddTo(mount: Int) {
        while (mount > diceValue.size) {
            diceValue += 0
        }
    }

    private fun mountMinusTo(mount: Int) {
        while (mount < diceValue.size) {
            diceValue.removeAt(diceValue.size - 1)
        }
    }
}