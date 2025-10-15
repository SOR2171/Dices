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

    fun deleteType(max: Int) {
        var index = 0
        for (i in diceList.indices) {
            if (diceList[i].max == max) {
                index = i
                break
            }
        }
        diceList.removeAt(index)
    }

    fun addDiceType(max: Int): Boolean {
        for (i in diceList.indices) {
            if (diceList[i].max == max)
                return false
        }
        diceList += DiceType(max)
        diceList.sortBy { diceType ->
            diceType.max
        }
        return true
    }
}