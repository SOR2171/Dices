package io.github.sor2171.dices.core.utils

import io.github.sor2171.dices.core.entity.DiceData
import io.github.sor2171.dices.core.entity.DiceType
import kotlin.collections.plusAssign

object DiceDataCollection {
    val diceList = mutableListOf(
        DiceType(6)
    )

    val diceDataList: List<DiceData>
        get() {
            val list = mutableListOf<DiceData>()
            for (diceType in diceList) {
                for (i in diceType.diceValue.indices) {
                    list += DiceData(
                        i + 1,
                        diceType.name,
                        diceType.diceValue[i]
                    )
                }
            }
            return list
        }

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
        if (max <= 1) return false
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