package io.github.sor2171.dices.core

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DiceSessionTest {
    @Test
    fun initialTrayHasStableIdsAndUnrolledValues() {
        val session = DiceSession()
        assertEquals("2D6 + 1D20", session.formula)
        assertEquals(3, session.totalDice)
        assertEquals(listOf("6-1", "6-2", "20-1"), session.dice.map { it.id })
        assertTrue(session.dice.all { it.value == 0 })
    }

    @Test
    fun invalidEditsLeaveConfigurationAndResultsUntouched() {
        val session = DiceSession { }
        assertTrue(startRoll(session).finished)
        val before = session.groups
        val record = session.lastRoll
        assertFalse(session.setCount(1, 1))
        assertFalse(session.setCount(1001, 1))
        assertFalse(session.setCount(6, -1))
        assertFalse(session.setCount(6, 21))
        assertFalse(session.usePreset(listOf(DiceGroup(6, 20), DiceGroup(8, 20), DiceGroup(20, 20), DiceGroup(100, 1))))
        assertEquals(before, session.groups)
        assertSame(record, session.lastRoll)
    }

    @Test
    fun duplicatePresetTypesAreMergedAndValidated() {
        val session = DiceSession()
        assertTrue(session.usePreset(listOf(DiceGroup(20, 2), DiceGroup(6, 3), DiceGroup(6, 2))))
        assertEquals(listOf(DiceGroup(6, 5), DiceGroup(20, 2)), session.groups)
        assertFalse(session.usePreset(listOf(DiceGroup(6, 20), DiceGroup(6, 1))))
        assertEquals(7, session.totalDice)
    }

    @Test
    fun addingExistingTypeIncrementsCountAndZeroRemovesIt() {
        val session = DiceSession()
        assertTrue(session.addType(6))
        assertEquals(DiceGroup(6, 3), session.groups.first())
        assertTrue(session.setCount(6, 0))
        assertEquals(listOf(DiceGroup(20, 1)), session.groups)
        assertTrue(session.setCount(20, 20))
        assertFalse(session.addType(20))
    }

    @Test
    fun presetInputCannotMutateThePublishedConfiguration() {
        val session = DiceSession()
        val input = mutableListOf(DiceGroup(8, 2))
        assertTrue(session.usePreset(input))
        input.clear()
        assertEquals(listOf(DiceGroup(8, 2)), session.groups)
    }

    @Test
    fun rollsProduceValuesInRangeAndKeepHistorySnapshots() {
        val session = DiceSession { }
        assertTrue(session.usePreset(listOf(DiceGroup(2, 20), DiceGroup(6, 20), DiceGroup(1000, 20))))
        repeat(10) {
            val completion = startRoll(session)
            assertTrue(completion.finished)
            assertNull(completion.error)
            val record = assertNotNull(session.lastRoll)
            assertEquals(60, record.dice.size)
            assertTrue(record.dice.all { it.value in 1..it.sides })
            assertEquals(record.dice.sumOf { it.value }, record.total)
        }
        val snapshot = session.history.first()
        val values = snapshot.dice.toList()
        assertTrue(session.setCount(6, 1))
        assertNull(session.lastRoll)
        assertEquals(values, snapshot.dice)
        assertEquals("20D2 + 20D6 + 20D1000", snapshot.formula)
        assertTrue(session.dice.all { it.value == 0 })
    }

    @Test
    fun identicalConfigurationPreservesCurrentRoll() {
        val session = DiceSession { }
        startRoll(session)
        val record = session.lastRoll
        assertTrue(session.usePreset(listOf(DiceGroup(20, 1), DiceGroup(6, 2))))
        assertSame(record, session.lastRoll)
    }

    @Test
    fun emptyTrayDoesNotStartAnimationOrCreateHistory() {
        var animationStarted = false
        val session = DiceSession { animationStarted = true }
        assertTrue(session.usePreset(emptyList()))
        assertTrue(startRoll(session).finished)
        assertFalse(animationStarted)
        assertFalse(session.isRolling)
        assertTrue(session.history.isEmpty())
    }

    @Test
    fun rollingGuardsRepeatedClicksAndConfigurationEdits() {
        val gate = AnimationGate()
        val session = DiceSession { gate.await() }
        val first = startRoll(session)
        assertFalse(first.finished)
        assertTrue(session.isRolling)
        assertFalse(session.addType(8))
        assertFalse(session.setCount(6, 0))
        assertFalse(session.usePreset(emptyList()))
        assertTrue(startRoll(session).finished)
        assertTrue(session.history.isEmpty())
        gate.finish()
        assertTrue(first.finished)
        assertNull(first.error)
        assertFalse(session.isRolling)
        assertEquals(1, session.history.size)
        assertEquals("2D6 + 1D20", session.history.single().formula)
    }

    @Test
    fun interruptedAnimationReleasesGuardWithoutPublishingAPartialRoll() {
        val gate = AnimationGate()
        val session = DiceSession { gate.await() }
        val completion = startRoll(session)
        val error = IllegalStateException("Animation interrupted")
        gate.fail(error)
        assertSame(error, completion.error)
        assertFalse(session.isRolling)
        assertTrue(session.history.isEmpty())
        assertNull(session.lastRoll)
        assertTrue(session.addType(8))
        val retry = startRoll(session)
        gate.finish()
        assertTrue(retry.finished)
        assertEquals(1, session.lastRoll?.id)
    }

    @Test
    fun historyIsBoundedAndClearingPreservesCurrentResultAndSequence() {
        val session = DiceSession { }
        repeat(55) { startRoll(session) }
        assertEquals(50, session.history.size)
        assertEquals(55, session.history.first().id)
        assertEquals(6, session.history.last().id)
        val current = session.lastRoll
        session.clearHistory()
        assertTrue(session.history.isEmpty())
        assertSame(current, session.lastRoll)
        startRoll(session)
        assertEquals(56, session.history.single().id)
    }

    private class RollCompletion {
        var finished = false
        var error: Throwable? = null
    }

    private fun startRoll(session: DiceSession): RollCompletion {
        val completion = RollCompletion()
        suspend { session.roll() }.startCoroutine(object : Continuation<Unit> {
            override val context = EmptyCoroutineContext
            override fun resumeWith(result: Result<Unit>) {
                completion.error = result.exceptionOrNull()
                completion.finished = true
            }
        })
        return completion
    }

    private class AnimationGate {
        private var continuation: Continuation<Unit>? = null

        suspend fun await() = suspendCoroutine<Unit> { continuation = it }

        fun finish() {
            val pending = checkNotNull(continuation)
            continuation = null
            pending.resume(Unit)
        }

        fun fail(error: Throwable) {
            val pending = checkNotNull(continuation)
            continuation = null
            pending.resumeWithException(error)
        }
    }
}