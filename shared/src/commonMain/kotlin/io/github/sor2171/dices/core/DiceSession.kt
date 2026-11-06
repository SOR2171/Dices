package io.github.sor2171.dices.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.delay
import kotlin.random.Random

/** One type of die in the current tray. Both values are validated by DiceSession. */
data class DiceGroup(val sides: Int, val count: Int)

/** An individual die. A value of zero means it has not been rolled yet. */
data class DieResult(val id: String, val sides: Int, val value: Int)

/** A detached snapshot, so editing the tray cannot rewrite an earlier roll. */
data class RollRecord(val id: Int, val formula: String, val dice: List<DieResult>) {
    val total: Int get() = dice.sumOf { it.value }
}

/**
 * Screen-owned, observable state. UI actions call these methods on the UI thread.
 * The internal constructor lets common tests advance the animation without real waiting.
 */
class DiceSession internal constructor(private val waitForAnimation: suspend () -> Unit) {
    constructor() : this({ delay(580) })

    var groups: List<DiceGroup> by mutableStateOf(listOf(DiceGroup(6, 2), DiceGroup(20, 1)))
        private set

    var history: List<RollRecord> by mutableStateOf(emptyList())
        private set

    var lastRoll: RollRecord? by mutableStateOf(null)
        private set

    var isRolling: Boolean by mutableStateOf(false)
        private set

    private var nextRollId = 1

    val totalDice: Int get() = groups.sumOf { it.count }

    val formula: String
        get() = groups.joinToString(" + ") { "${it.count}D${it.sides}" }

    val dice: List<DieResult>
        get() = lastRoll?.dice ?: groups.flatMap { group ->
            List(group.count) { index -> DieResult("${group.sides}-${index + 1}", group.sides, 0) }
        }

    fun setCount(sides: Int, count: Int): Boolean {
        if (sides !in 2..MAX_SIDES || count !in 0..MAX_COUNT) return false
        val updated = groups.filterNot { it.sides == sides } + DiceGroup(sides, count)
        return usePreset(updated)
    }

    /** Adding an existing type adds one die rather than creating a duplicate group. */
    fun addType(sides: Int): Boolean {
        val currentCount = groups.firstOrNull { it.sides == sides }?.count ?: 0
        return setCount(sides, currentCount + 1)
    }

    /** Validation happens before publication, so a rejected edit leaves the tray intact. */
    fun usePreset(groups: List<DiceGroup>): Boolean {
        if (isRolling) return false
        val counts = mutableMapOf<Int, Int>()
        var total = 0
        for (group in groups) {
            if (group.sides !in 2..MAX_SIDES || group.count !in 0..MAX_COUNT) return false
            val combinedCount = (counts[group.sides] ?: 0) + group.count
            if (combinedCount > MAX_COUNT) return false
            total += group.count
            if (total > MAX_DICE) return false
            counts[group.sides] = combinedCount
        }
        val normalized = counts.entries
            .filter { it.value > 0 }
            .sortedBy { it.key }
            .map { DiceGroup(it.key, it.value) }
        if (normalized == this.groups) return true
        Snapshot.withMutableSnapshot {
            this.groups = normalized
            lastRoll = null
        }
        return true
    }

    suspend fun roll() {
        if (isRolling || totalDice == 0) return
        // Set the guard before suspending. Repeated clicks cannot start another roll.
        isRolling = true
        val rollGroups = groups.toList()
        val rollFormula = formula
        try {
            waitForAnimation()
            val results = rollGroups.flatMap { group ->
                List(group.count) { index ->
                    DieResult(
                        id = "${group.sides}-${index + 1}",
                        sides = group.sides,
                        value = Random.nextInt(1, group.sides + 1),
                    )
                }
            }
            val record = RollRecord(nextRollId, rollFormula, results)
            Snapshot.withMutableSnapshot {
                lastRoll = record
                history = (listOf(record) + history).take(HISTORY_LIMIT)
                isRolling = false
            }
            nextRollId++
        } finally {
            // Cancellation while navigating away or closing must release the guard.
            isRolling = false
        }
    }

    fun clearHistory() {
        history = emptyList()
    }

    companion object {
        const val MAX_SIDES = 1000
        const val MAX_COUNT = 20
        const val MAX_DICE = 60
        private const val HISTORY_LIMIT = 50
    }
}