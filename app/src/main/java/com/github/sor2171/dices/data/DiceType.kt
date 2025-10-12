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
}