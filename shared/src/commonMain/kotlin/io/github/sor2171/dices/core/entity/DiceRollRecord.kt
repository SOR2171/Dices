package io.github.sor2171.dices.core.entity

data class DiceRollRecord (
    val diceResults: List<DiceData>,
    val totalSum: Int
)