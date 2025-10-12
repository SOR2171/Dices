package com.github.sor2171.dices.data

object DiceDataCollection {
    val diceList = mutableListOf(
        DiceType(6)
    )

    fun rollAll() {
        for (dt in diceList) {
            dt.roll()
        }
    }
}